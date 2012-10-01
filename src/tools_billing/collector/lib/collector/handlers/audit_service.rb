# Copyright (c) 2009-2012 VMware, Inc.

module Collector

  class Handler
    class AuditService < BillingHandler
      register AUDIT_SERVICE_COMPONENT

      def process(varz)
        super(varz)
      end
    end
  end
end
