# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  class Handler
    class RedisNode < ServiceHandler
      register REDIS_NODE

      def process(varz)
        process_healthy_instances_metric(varz)
      end

      def service_type
        "redis"
      end

      def component
        "node"
      end

    end
  end
end
