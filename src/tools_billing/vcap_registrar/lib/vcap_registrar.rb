$:.unshift(File.expand_path(".", File.dirname(__FILE__)))

require "rubygems"
require "bundler/setup"

require "eventmachine"
require "nats/client"
require "vcap/logging"
require "securerandom"

module VcapRegistrar

  class Config
    class << self
      [:logger, :nats_uri, :type, :host, :port, :username, :password, :uri, :tags, :uuid, :index].each { |option| attr_accessor option }

      def configure(config)
        @logger = VCAP::Logging.logger("vcap_registrar")

        @nats_uri = config["mbus"]

        @host = config["host"]
        @port = config["port"]
        @uri = config["uri"]
        @tags = config["tags"]
        @type = config["varz"]["type"]
        @username = config["varz"]["username"]
        @password = config["varz"]["password"]
        @uuid = config["varz"]["uuid"] || SecureRandom.uuid
        @index = config["index"] || 0
      end
    end
  end

  class VcapRegistrar
    DISCOVER_TOPIC = "vcap.component.discover"
    ANNOUNCE_TOPIC = "vcap.component.announce"
    ROUTER_START_TOPIC = "router.start"
    ROUTER_REGISTER_TOPIC = "router.register"
    ROUTER_UNREGISTER_TOPIC = "router.unregister"

    def initialize
      @logger = Config.logger

      NATS.on_error do |e|
        @logger.fatal("Exiting, NATS error")
        @logger.fatal(e)
        exit
      end
    end

    def register_varz_credentials()
      @discover_msg = Yajl::Encoder.encode({
        :type => Config.type,
        :host => "#{Config.host}:#{Config.port}",
        :index => Config.index,
        :uuid => "#{Config.index}-#{Config.uuid}",
        :credentials => [Config.username, Config.password]
      })

      unless (Config.username.nil? || Config.password.nil?)
        @nats = NATS.connect(:uri => Config.nats_uri) do
          @logger.info("Connected to NATS - varz registration")

          @nats.subscribe(DISCOVER_TOPIC) do |msg, reply|
            @logger.debug("Received #{DISCOVER_TOPIC} publishing #{reply.inspect} #{@discover_msg.inspect}")
            @nats.publish(reply, @discover_msg)
          end

          @logger.info("Announcing start up #{ANNOUNCE_TOPIC}")
          @nats.publish(ANNOUNCE_TOPIC, @discover_msg)
        end
      else
        @logger.error("Could not register nil varz credentials")
      end
    end


    def register_with_router()
      @registration_message = Yajl::Encoder.encode({
        :host => Config.host,
        :port => Config.port,
        :uris => Array(Config.uri),
        :tags => Config.tags
      })

      @nats = NATS.connect(:uri => Config.nats_uri) do
        @logger.info("Connected to NATS - router registration")

        @nats.subscribe(ROUTER_START_TOPIC) do
          @logger.debug("Sending registration: #{@registration_message}")
          send_registration_message
        end
        @logger.info("Sending registration: #{@registration_message}")
        send_registration_message
      end

    end

    def shutdown(&block)
      send_unregistration_message(&block)
    end

    def send_registration_message
      @nats.publish(ROUTER_REGISTER_TOPIC, @registration_message)
    end

    def send_unregistration_message(&block)
      @logger.info("Sending unregistration: #{@registration_message}")
      @nats.publish(ROUTER_UNREGISTER_TOPIC, @registration_message, &block)
    end
  end
end
