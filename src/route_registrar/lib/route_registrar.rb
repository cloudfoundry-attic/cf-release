require "eventmachine"
require "steno"
require "securerandom"
require "cf_message_bus/message_bus"

class RouteRegistrar
  ROUTER_START_TOPIC = "router.start"
  ROUTER_GREET_TOPIC = "router.greet"
  ROUTER_REGISTER_TOPIC = "router.register"
  ROUTER_UNREGISTER_TOPIC = "router.unregister"

  attr_reader :logger, :message_bus_servers, :type, :host, :port_map,
    :username, :password, :tags, :uuid, :index, :private_instance_id, :update_frequency_in_seconds

  def initialize(route_registry_config, port_map_config)
    @logger = Steno.logger("route_registrar")

    route_registry_config = symbolize_keys(route_registry_config)
    port_map_config = symbolize_keys(port_map_config)

    @message_bus_servers = route_registry_config[:message_bus_servers]
    @host = route_registry_config[:host]
    @port_map = port_map_config[:port_map]
    @tags = route_registry_config[:tags]
    @index = route_registry_config[:index] || 0
    @update_frequency_in_seconds = route_registry_config[:update_frequency_in_seconds]
    @private_instance_id = route_registry_config[:private_instance_id] || SecureRandom.uuid
  end

  def register_with_router
    logger.info("Connected to NATS - router registration")

    send_registration_messages
    @registration_timer = EM.add_periodic_timer(update_frequency_in_seconds) do
      send_registration_messages
    end
  end

  def shutdown(&block)
    EM.cancel_timer(@registration_timer) if @registration_timer
    send_unregistration_messages(&block)
  end

  private

  def message_bus
    @message_bus ||= CfMessageBus::MessageBus.new(
      servers: message_bus_servers,
      logger: logger)
  end

  def send_registration_messages
    registry_messages.each do |registry_message|
      logger.debug("Sending registration: #{registry_message}")
      message_bus.publish(ROUTER_REGISTER_TOPIC, registry_message)
    end
  end

  def send_unregistration_messages(&block)
    registry_messages.each do |registry_message|
      logger.info("Sending unregistration: #{registry_message}")
      message_bus.publish(ROUTER_UNREGISTER_TOPIC, registry_message, &block)
    end
  end

  def registry_messages
    port_map.collect do |port, uris|
      {
        :host => host,
        :port => port.to_i,
        :uris => Array(uris),
        :tags => tags,
        :private_instance_id => private_instance_id
      }
    end

  end

  def symbolize_keys(hash)
    hash.inject({}) do |hsh, pair|
      hsh[pair[0].to_sym] = pair[1]
      hsh
    end
  end
end
