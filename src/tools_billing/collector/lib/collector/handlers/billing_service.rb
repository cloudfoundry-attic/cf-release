# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  BILLING_SERVICE_COMPONENT = "BillingService"

  class Handler
    class BillingService < BillingHandler
      register BILLING_SERVICE_COMPONENT

      def process(varz)
        super(varz)
      end
    end
  end
end
