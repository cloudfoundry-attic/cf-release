# Copyright (c) 2009-2012 VMware, Inc.

$:.unshift(File.expand_path(".", File.dirname(__FILE__)))

require "base64"
require "set"

require "rubygems"
require "bundler/setup"

require "em-http-request"
require "eventmachine"
require "nats/client"
require "vcap/logging"
require "vcap/rolling_metric"

require "collector/config"
require "collector/handler"
require "collector/service_handler"
require "collector/tsdb_connection"

module Collector

  CLOUD_CONTROLLER_COMPONENT = "CloudController"
  DEA_COMPONENT = "DEA"
  HEALTH_MANAGER_COMPONENT = "HealthManager"
  ROUTER_COMPONENT = "Router"

  # services components
  MYSQL_PROVISIONER = "MyaaS-Provisioner"
  MYSQL_NODE = "MyaaS-Node"

  PGSQL_PROVISIONER = "AuaaS-Provisioner"
  PGSQL_NODE = "AuaaS-Node"

  MONGODB_PROVISIONER = "MongoaaS-Provisioner"
  MONGODB_NODE = "MongoaaS-Node"

  NEO4J_PROVISIONER = "Neo4jaaS-Provisioner"
  NEO4J_NODE = "Neo4jaaS-Node"

  RABBITMQ_PROVISIONER = "RMQaaS-Provisioner"
  RABBITMQ_NODE = "RMQaaS-Node"

  REDIS_PROVISIONER = "RaaS-Provisioner"
  REDIS_NODE = "RaaS-Node"

  VBLOB_PROVISIONER = "VBlobaaS-Provisioner"
  VBLOB_NODE = "VBlobaaS-Node"

  SERIALIZATION_DATA_SERVER = "SerializationDataServer"

  BACKUP_MANAGER = "BackupManager"

  # Varz collector
  class Collector
    ANNOUNCE_SUBJECT = "vcap.component.announce"
    DISCOVER_SUBJECT = "vcap.component.discover"

    # Creates a new varz collector based on the {Config} settings.
    def initialize
      Dir[File.join(File.dirname(__FILE__),
                    "../lib/collector/handlers/*.rb")].each do |file|
        require File.join("collector/handlers",
                          File.basename(file, File.extname(file)))
      end

      @logger = Config.logger

      @components = {}
      @core_components = Set.new([CLOUD_CONTROLLER_COMPONENT, DEA_COMPONENT,
                                  HEALTH_MANAGER_COMPONENT, ROUTER_COMPONENT])
      @service_components = Set.new([MYSQL_PROVISIONER, MYSQL_NODE,
                                     PGSQL_PROVISIONER, PGSQL_NODE,
                                     MONGODB_PROVISIONER, MONGODB_NODE,
                                     NEO4J_PROVISIONER, NEO4J_NODE,
                                     RABBITMQ_PROVISIONER, RABBITMQ_NODE,
                                     REDIS_PROVISIONER, REDIS_NODE,
                                     VBLOB_PROVISIONER, VBLOB_NODE])
      @service_auxiliary_components = Set.new([SERIALIZATION_DATA_SERVER,
                                               BACKUP_MANAGER])

      @tsdb_connection = EventMachine.connect(
          Config.tsdb_host, Config.tsdb_port, TsdbConnection)
      @nats_latency = VCAP::RollingMetric.new(60)

      NATS.on_error do |e|
        @logger.fatal("Exiting, NATS error")
        @logger.fatal(e)
        exit
      end

      @nats = NATS.connect(:uri => Config.nats_uri) do
        @logger.info("Connected to NATS")
        # Send initially to discover what's already running
        @nats.subscribe(ANNOUNCE_SUBJECT) do |message|
          process_component_discovery(message)
        end

        @inbox = NATS.create_inbox
        @nats.subscribe(@inbox) {|message| process_component_discovery(message)}

        @nats.publish(DISCOVER_SUBJECT, "", @inbox)

        @nats.subscribe("collector.nats.ping") do |message|
          process_nats_ping(message.to_f)
        end

        setup_timers
      end
    end

    # Configures the periodic timers for collecting varzs.
    def setup_timers
      EM.add_periodic_timer(Config.discover_interval) do
        @nats.publish(DISCOVER_SUBJECT, "", @inbox)
      end

      EM.add_periodic_timer(Config.varz_interval) { fetch_varz }
      EM.add_periodic_timer(Config.healthz_interval) { fetch_healthz }
      EM.add_periodic_timer(Config.prune_interval) { prune_components }

      EM.add_periodic_timer(Config.local_metrics_interval) do
        send_local_metrics
      end

      EM.add_periodic_timer(Config.nats_ping_interval) do
        @nats.publish("collector.nats.ping", Time.now.to_f.to_s)
      end
    end

    # Processes NATS ping in order to calculate NATS roundtrip latency
    #
    # @param [Float] ping_timestamp UNIX timestamp when the ping was sent
    def process_nats_ping(ping_timestamp)
      @nats_latency << ((Time.now.to_f - ping_timestamp) * 1000).to_i
    end

    # Processes a discovered component message, recording it's location for
    # varz/healthz probes.
    #
    # @param [Hash] message the discovery message
    def process_component_discovery(message)
      message = Yajl::Parser.parse(message)
      if message["index"]
        @logger.debug1("Found #{message["type"]}/#{message["index"]} @ " +
                           " #{message["host"]} #{message["credentials"]}")
        instances = (@components[message["type"]] ||= {})
        instances[message["index"]] = {
          :host => message["host"],
          :credentials => message["credentials"],
          :timestamp => Time.now.to_i
        }
      end
    rescue Exception => e
      @logger.warn("Error discovering components: #{e.message}")
      @logger.warn(e)
    end

    # Prunes components that haven't been heard from in a while
    def prune_components
      @components.each do |_, instances|
        instances.delete_if do |_, component|
          Time.now.to_i - component[:timestamp] > Config.prune_interval
        end
      end

      @components.delete_if { |_, instances| instances.empty? }
    rescue => e
      @logger.warn("Error pruning components: #{e.message}")
      @logger.warn(e)
    end

    # Generates metrics that don't require any interactions with varz or healthz
    def send_local_metrics
      handler = Handler.handler(@tsdb_connection, "collector", Config.index,
                                Time.now.to_i)
      handler.send_latency_metric("nats.latency.1m", @nats_latency.value)
    end

    # Fetches the varzs from all the components and calls the proper {Handler}
    # to record the metrics in the TSDB server
    def fetch_varz
      @components.each do |job, instances|
        instances.each do |index, instance|
          next unless credentials_ok?(job, instance)
          varz_uri = "http://#{instance[:host]}/varz"
          http = EventMachine::HttpRequest.new(varz_uri).get(
                  :head => authorization_headers(instance))
          http.errback do
            @logger.warn("Failed fetching varz from: #{instance[:host]}")
          end
          http.callback do
            begin
              varz = Yajl::Parser.parse(http.response)
              now = Time.now.to_i

              handler = Handler.handler(@tsdb_connection, job, index, now)
              if varz["mem"]
                handler.send_metric("mem", varz["mem"] / 1024,
                                    get_job_tags(job))
              end
              handler.process(varz)
            rescue => e
              @logger.warn("Error processing varz: #{e.message}")
              @logger.warn(e)
            end
          end
        end
      end
    end

    # Fetches the healthz from all the components and calls the proper {Handler}
    # to record the metrics in the TSDB server
    def fetch_healthz
      @components.each do |job, instances|
        instances.each do |index, instance|
          next unless credentials_ok?(job, instance)
          healthz_uri = "http://#{instance[:host]}/healthz"
          http = EventMachine::HttpRequest.new(healthz_uri).get(
                  :head => authorization_headers(instance))
          http.errback do
            @logger.warn("Failed fetching healthz from: #{instance[:host]}")
          end
          http.callback do
            begin
              now = Time.now.to_i
              handler = Handler.handler(@tsdb_connection, job, index, now)
              is_healthy = http.response.strip.downcase == "ok" ? 1 : 0
              handler.send_metric("healthy", is_healthy, get_job_tags(job))
            rescue => e
              handler.send_metric("healthy", 0, get_job_tags(job))
              @logger.warn("Error processing healthz: #{e.message}")
              @logger.warn(e)
            end
          end
        end
      end
    end

    # Generates the common tags used for generating common
    # (memory, health, etc.) metrics.
    #
    # @param [String] type the job type
    # @return [Hash<Symbol, String>] tags for this job type
    def get_job_tags(type)
      tags = {}
      if @core_components.include?(type)
        tags[:role] = "core"
      elsif @service_components.include?(type)
        tags[:role] = "service"
      elsif @service_auxiliary_components.include?(type)
        tags[:role] = "service"
      end
      tags
    end

    def credentials_ok?(job, instance)
      unless instance[:credentials].kind_of?(Array)
        @logger.warn("Bad credentials from #{job.inspect} #{instance.inspect}")
        return false
      end
      true
    end

    # Generates the authorization headers for a specific instance
    #
    # @param [Hash] instance hash
    # @return [Hash] headers
    def authorization_headers(instance)
      credentials = Base64.strict_encode64(instance[:credentials].join(":"))

      {
        "Authorization" => "Basic #{credentials}"
      }
    end

  end
end
