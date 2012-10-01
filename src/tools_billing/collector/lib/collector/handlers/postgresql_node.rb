# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  class Handler
    class PostgresqlNode < ServiceHandler
      register PGSQL_NODE

      def process(varz)
        process_healthy_instances_metric(varz)
      end

      def service_type
        "postgresql"
      end

      def component
        "node"
      end

    end
  end
end
