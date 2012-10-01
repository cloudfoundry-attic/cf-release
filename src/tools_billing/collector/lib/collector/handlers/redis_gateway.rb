# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  class Handler
    class RedisProvisioner < ServiceHandler
      register REDIS_PROVISIONER

      def process(varz)
        process_plan_score_metric(varz)
        process_online_nodes(varz)
      end

      def service_type
        "redis"
      end

      def component
        "gateway"
      end

    end
  end
end
