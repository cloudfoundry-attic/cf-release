# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  class Handler
    class MongodbNode < ServiceHandler
      register MONGODB_NODE

      def process(varz)
        process_healthy_instances_metric(varz)
      end

      def service_type
        "mongodb"
      end

      def component
        "node"
      end

    end
  end
end
