# Copyright (c) 2009-2012 VMware, Inc.

require File.expand_path("../../spec_helper", File.dirname(__FILE__))

describe Collector::ServiceHandler do

  describe :send_metric do
    it "should send the metric to the TSDB serveri with service & component" \
       "tag" do
      connection = mock(:TsdbConnection)
      connection.should_receive(:send_data).
          with("put some_key 10000 2 component=unknown index=1 " \
               "job=Test service_type=unknown tag=value\n")
      handler = Collector::ServiceHandler.new(connection, "Test", 1, 10000)
      handler.send_metric("some_key", 2, {:tag => "value"})
    end
  end

  describe :process_healthy_instances_metric do
    it "should report healthy instances percentage metric to TSDB server" do
      connection = mock(:TsdbConnection)
      connection.should_receive(:send_data).
          with("put services.healthy_instances 10000 50.00 component=unknown " \
               "index=1 job=Test service_type=unknown\n")
      handler = Collector::ServiceHandler.new(connection, "Test", 1, 10000)
      varz = {
               "instances" => {
                  1 => 'ok',
                  2 => 'fail',
                  3 => 'fail',
                  4 => 'ok'
               }
      }
      handler.process_healthy_instances_metric(varz)
    end
  end

  describe :process_plan_score_metric do
    it "should report low_water & high_water & score metric to TSDB server" do
      connection = mock(:TsdbConnection)
      connection.should_receive(:send_data).
         with("put services.plans.high_water 10000 1400 component=unknown " \
              "index=1 job=Test plan=free service_type=unknown\n")
      connection.should_receive(:send_data).
         with("put services.plans.low_water 10000 100 component=unknown " \
              "index=1 job=Test plan=free service_type=unknown\n")
      connection.should_receive(:send_data).
         with("put services.plans.score 10000 150 component=unknown " \
              "index=1 job=Test plan=free service_type=unknown\n")
      connection.should_receive(:send_data).
         with("put services.plans.allow_over_provisioning 10000 0 " \
              "component=unknown index=1 job=Test plan=free " \
              "service_type=unknown\n")
      handler = Collector::ServiceHandler.new(connection, "Test", 1, 10000)
      varz = {
               "plans" => [
                 { "plan" => "free",
                   "low_water" => 100,
                   "high_water" => 1400,
                   "score" => 150 }
               ]
      }
      handler.process_plan_score_metric(varz)
    end
  end

  describe :process_online_nodes do
    it "should report online nodes number to TSDB server" do
      connection = mock(:TsdbConnection)
      connection.should_receive(:send_data).
         with("put services.online_nodes 10000 2 component=unknown index=1 " \
              "job=Test service_type=unknown\n")
      handler = Collector::ServiceHandler.new(connection, "Test", 1, 10000)
      varz = {
               "nodes" => {
                 "node_0" => {
                   "available_capacity" => 50,
                   "plan" => "free"
                 },
                 "node_1" => {
                   "available_capacity" => 50,
                   "plan" => "free"
                 }
               }
      }
      handler.process_online_nodes(varz)
    end
  end

end
