# Copyright (c) 2009-2012 VMware, Inc.

module Collector

  class Handler
    class QuotaEnforcer < BillingHandler
      register QUOTA_ENFORCER_COMPONENT

      def process(varz)
        super(varz)
      end
    end
  end
end
