# Copyright (c) 2009-2012 VMware, Inc.

require File.expand_path("../../spec_helper", File.dirname(__FILE__))

describe Collector::Handler do

  describe :register do
    after(:each) do
      Collector::Handler.handler_map.clear
    end

    it "should register varz handler plugins" do
      test_handler = Class.new(Collector::Handler) do
        register "Test"
      end

      Collector::Handler.handler_map.should == {"Test" => test_handler}
    end


    it "should fail to register multiple handlers for a single job" do
      Class.new(Collector::Handler) do
        register "Test"
      end

      lambda {
        Class.new(Collector::Handler) do
          register "Test"
        end
      }.should raise_exception "Job: Test already registered"
    end
  end

  describe :handler do
    after(:each) do
      Collector::Handler.handler_map.clear
    end

    it "should return the registered varz handler plugin" do
      test_handler = Class.new(Collector::Handler) do
        register "Test"
      end

      Collector::Handler.handler(nil, "Test", nil, nil).
          should be_kind_of(test_handler)
    end

    it "should return the default handler when none registered" do
      Collector::Handler.handler(nil, "Test", nil, nil).
          should be_kind_of(Collector::Handler)
    end
  end

  describe :send_metric do
    it "should send the metric to the TSDB server" do
      connection = mock(:TsdbConnection)
      connection.should_receive(:send_data).
          with("put some_key 10000 2 index=1 job=Test tag=value\n")
      handler = Collector::Handler.handler(connection, "Test", 1, 10000)
      handler.send_metric("some_key", 2, {:tag => "value"})
    end
  end

  describe :send_latency_metric do
    it "should send the metric to the TSDB server" do
      connection = mock(:TsdbConnection)
      connection.should_receive(:send_data).
          with("put latency_key 10000 5 index=1 job=Test tag=value\n")
      handler = Collector::Handler.handler(connection, "Test", 1, 10000)
      handler.send_latency_metric("latency_key",
                                  {"value" => 10, "samples" => 2},
                                  {:tag => "value"})
    end
  end

end