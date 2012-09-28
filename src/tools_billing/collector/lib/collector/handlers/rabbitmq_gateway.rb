# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  class Handler
    class RabbitmqProvisioner < ServiceHandler
      register RABBITMQ_PROVISIONER

      def process(varz)
        process_plan_score_metric(varz)
        process_online_nodes(varz)
      end

      def service_type
        "rabbitmq"
      end

      def component
        "gateway"
      end

    end
  end
end
