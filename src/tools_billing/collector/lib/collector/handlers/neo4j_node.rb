# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  class Handler
    class Neo4jNode < ServiceHandler
      register NEO4J_NODE

      def process(varz)
        process_healthy_instances_metric(varz)
      end

      def service_type
        "neo4j"
      end

      def component
        "node"
      end

    end
  end
end
