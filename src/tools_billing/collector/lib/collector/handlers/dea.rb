# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  class Handler
    class Dea < Handler
      register DEA_COMPONENT

      def process(varz)
        byte_metrics = %w(used_memory reserved_memory used_disk)
        if varz["frameworks"]
          varz["frameworks"].each do |framework, metrics|
            byte_metrics.each do |metric_name|
              send_metric("frameworks.#{metric_name}",
                          metrics[metric_name] / 1024, :framework => framework)
            end
            send_metric("frameworks.used_cpu", metrics["used_cpu"],
                        :framework => framework)
          end
        end

        if varz["runtimes"]
          varz["runtimes"].each do |runtime, metrics|
            byte_metrics.each do |metric_name|
              send_metric("runtimes.#{metric_name}",
                          metrics[metric_name] / 1024, :runtime => runtime)
            end
            send_metric("runtimes.used_cpu", metrics["used_cpu"],
                        :runtime => runtime)
          end
        end
        send_metric("dea.max_memory", varz["apps_max_memory"])
      end
    end
  end
end
