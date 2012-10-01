# Copyright (c) 2009-2012 VMware, Inc.

module Collector

  class Handler
    class EventAggregator < BillingHandler
      register EVENT_AGGREGATOR_COMPONENT

      def process(varz)
        super(varz)
      end
    end
  end
end
