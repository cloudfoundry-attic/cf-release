# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  # TSDB connection for sending metrics
  class TsdbConnection < EventMachine::Connection
    def post_init
      @logger = Config.logger
    end

    def connection_completed
      @logger.info("Connected to TSDB server")
      @port, @ip = Socket.unpack_sockaddr_in(get_peername)
    end

    def unbind
      if @port && @ip
        @logger.warn("Lost connection to TSDB server, reconnecting")
        EM.add_timer(1.0) do
          begin
            reconnect(@ip, @port)
          rescue EventMachine::ConnectionError => e
            @logger.warn(e)
            unbind
          end
        end
      else
        @logger.fatal("Couldn't connect to TSDB server, exiting.")
        exit!
      end
    end

    def receive_data(data)
      @logger.debug("Received from TSDB: #{data}")
    end
  end
end
