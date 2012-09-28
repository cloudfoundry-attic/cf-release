# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  class Handler
    class HealthManager < Handler
      register HEALTH_MANAGER_COMPONENT

      METRICS = {
              "total" => {
                      "apps" => "apps",
                      "started_apps" => "started_apps",
                      "instances" => "instances",
                      "started_instances" => "started_instances",
                      "memory" => "memory",
                      "started_memory" => "started_memory"
              },
              "running" => {
                      "apps" => "running_apps",
                      "crashes" => "crashes",
                      "running_instances" => "running_instances",
                      "missing_instances" => "missing_instances",
                      "flapping_instances" => "flapping_instances"
              }
      }

      def process(varz)
        METRICS.each do |type, metric_map|
          if type_varz = varz[type]
            if framework_varz = type_varz["frameworks"]
              framework_varz.each do |framework, metrics|
                metric_map.each do |varz_name, metric_name|
                  if metrics[varz_name]
                    send_metric("frameworks.#{metric_name}", metrics[varz_name],
                                :framework => framework)
                  end
                end
              end
            end

            if runtime_varz = type_varz["runtimes"]
              runtime_varz.each do |runtime, metrics|
                metric_map.each do |varz_name, metric_name|
                  if metrics[varz_name]
                    send_metric("runtimes.#{metric_name}", metrics[varz_name],
                                :runtime => runtime)
                  end
                end
              end
            end
          end
        end

        send_metric("total_users", varz["total_users"]) if varz["total_users"]
      end
    end
  end
end
