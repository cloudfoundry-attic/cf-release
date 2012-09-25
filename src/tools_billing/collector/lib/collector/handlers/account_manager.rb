# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  ACCOUNT_MANAGER_COMPONENT = "AccountManager"

  class Handler
    class AccountManager < Handler
      register ACCOUNT_MANAGER_COMPONENT

      def process(varz)
        simple_metrics = %w(requests success cpu uptime)
        simple_metrics.each do |metric|
          send_metric("account_manager.#{metric}", varz[metric])
        end
        if varz['errors']
          varz['errors'].each do |error_id, count|
            send_metric("account_manager.errors.#{error_id}", count)
          end
        end
        if varz['stats']
          varz['stats'].each do |statline|
            send_metric("account_manager.stats.#{statline}", 0) # varz['stats'][statline].last # app dependent formatting
          end
        end
      end
    end
  end
end
