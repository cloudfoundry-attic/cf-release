# Copyright (c) 2009-2012 VMware, Inc.

module Collector

  class Handler
    class AccountManager < BillingHandler
      register ACCOUNT_MANAGER_COMPONENT

      def process(varz)
        super(varz)
      end
    end
  end
end
