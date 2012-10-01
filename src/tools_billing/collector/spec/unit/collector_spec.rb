# Copyright (c) 2009-2012 VMware, Inc.

require File.expand_path("../spec_helper", File.dirname(__FILE__))

describe Collector::Collector do

  def create_fake_collector
    Collector::Config.tsdb_host = "dummy"
    Collector::Config.tsdb_port = 14242
    Collector::Config.nats_uri = "nats://foo:bar@nats-host:14222"

    tsdb_connection = mock(:TsdbConnection)
    EventMachine.should_receive(:connect).
        with("dummy", 14242, Collector::TsdbConnection).
        and_return(tsdb_connection)

    nats_connection = mock(:NatsConnection)
    NATS.should_receive(:connect).
        with(:uri => "nats://foo:bar@nats-host:14222").
        and_return(nats_connection)

    yield Collector::Collector.new, tsdb_connection, nats_connection
  end

  describe :process_component_discovery do
    it "should record components when they announce themeselves" do
      create_fake_collector do |collector, _, _|
        components = collector.instance_eval { @components }
        components.should be_empty

        Time.should_receive(:now).at_least(1).and_return(Time.at(1311979380))

        collector.process_component_discovery(Yajl::Encoder.encode({
          "type" => "Test",
          "index" => 1,
          "host" => "test-host:1234",
          "credentials" => ["user", "pass"]
        }))

        components.should == {
          "Test"=> {
            1 => {
              :host=>"test-host:1234",
              :credentials=>["user", "pass"],
              :timestamp=>1311979380
            }
          }
        }
      end
    end
  end

  describe :prune_components do
    it "should prune old components" do
      create_fake_collector do |collector, _, _|
        Collector::Config.prune_interval = 10

        components = collector.instance_eval { @components }
        components.should be_empty

        collector.process_component_discovery(Yajl::Encoder.encode({
          "type" => "Test",
          "index" => 1,
          "host" => "test-host:1234",
          "credentials" => ["user", "pass"]
        }))

        collector.process_component_discovery(Yajl::Encoder.encode({
          "type" => "Test",
          "index" => 2,
          "host" => "test-host:1234",
          "credentials" => ["user", "pass"]
        }))

        components["Test"][1][:timestamp] = 100000
        components["Test"][2][:timestamp] = 100005

        Time.should_receive(:now).at_least(1).and_return(Time.at(100011))

        collector.prune_components

        components.should == {
          "Test"=> {
            2 => {
              :host=>"test-host:1234",
              :credentials=>["user", "pass"],
              :timestamp=>100005
            }
          }
        }
      end
    end
  end

  describe :fetch_varz do
    def test_varz
      create_fake_collector do |collector, tsdb_connection, _|
        collector.process_component_discovery(Yajl::Encoder.encode({
          "type" => "Test",
          "index" => 1,
          "host" => "test-host:1234",
          "credentials" => ["user", "pass"]
        }))

        http_request = mock(:HttpRequest)
        http_request.should_receive(:errback)

        callback = nil
        http_request.should_receive(:callback).and_return do |block|
          callback = block
        end

        http_client = mock(:HttpClient)
        http_client.should_receive(:get).
            with({:head=>{"Authorization" => "Basic dXNlcjpwYXNz"}}).
            and_return(http_request)

        EventMachine::HttpRequest.should_receive(:new).
            with("http://test-host:1234/varz").
            and_return(http_client)

        collector.fetch_varz

        callback.should_not be_nil
        handler = mock(:Handler)

        yield(http_request, handler)

        Collector::Handler.should_receive(:handler).
            with(tsdb_connection, "Test", 1, kind_of(Fixnum)).
            and_return(handler)
        callback.call
      end
    end


    it "should fetch the varz from the component and use a " +
           "handler to process the results" do
      test_varz do |http_request, handler|
        http_request.should_receive(:response).and_return(Yajl::Encoder.encode({
          "mem" => 1234,
          "test" => "foo"
        }))

        handler.should_receive(:send_metric).with("mem", 1, {})
        handler.should_receive(:process).with({"mem" => 1234, "test" => "foo"})
      end
    end

    it "should skip memory metric if not there" do
      test_varz do |http_request, handler|
        http_request.should_receive(:response).and_return(Yajl::Encoder.encode({
          "test" => "foo"
        }))

        handler.should_receive(:process).with({"test" => "foo"})
      end
    end
  end

  describe :fetch_healthz do

    def setup_healthz_request
      create_fake_collector do |collector, tsdb_connection, _|
        collector.process_component_discovery(Yajl::Encoder.encode({
          "type" => "Test",
          "index" => 1,
          "host" => "test-host:1234",
          "credentials" => ["user", "pass"]
        }))

        http_request = mock(:HttpRequest)
        http_request.should_receive(:errback)

        callback = nil
        http_request.should_receive(:callback).and_return do |block|
          callback = block
        end

        http_client = mock(:HttpClient)
        http_client.should_receive(:get).
            with({:head=>{"Authorization" => "Basic dXNlcjpwYXNz"}}).
            and_return(http_request)

        EventMachine::HttpRequest.should_receive(:new).
            with("http://test-host:1234/healthz").
            and_return(http_client)

        collector.fetch_healthz

        callback.should_not be_nil

        handler = mock(:Handler)

        yield http_request, handler

        Collector::Handler.should_receive(:handler).
            with(tsdb_connection, "Test", 1, kind_of(Fixnum)).
            and_return(handler)
        callback.call
      end
    end

    it "should fetch the healthz from the component and " +
           "mark healthy instances" do
      setup_healthz_request do |http_request, handler|
        http_request.should_receive(:response).and_return("ok")
        handler.should_receive(:send_metric).with("healthy", 1, {})
      end
    end

    it "should fetch the healthz from the component and " +
           "mark unhealthy instances" do
      setup_healthz_request do |http_request, handler|
        http_request.should_receive(:response).and_return("not ok")
        handler.should_receive(:send_metric).with("healthy", 0, {})
      end
    end

  end

  describe :local_metrics do
    def send_local_metrics
      Time.stub!(:now).and_return(1000)

      create_fake_collector do |collector, tsdb, nats|
        collector.process_nats_ping(997)
        collector.process_nats_ping(998)
        collector.process_nats_ping(999)

        handler = mock(:Handler)
        yield handler

        Collector::Handler.should_receive(:handler).
          with(tsdb, "collector", 0, 1000).
          and_return(handler)

        collector.send_local_metrics
      end
    end

    it "should send nats latency rolling metric" do
      send_local_metrics do |handler|
        latency = {:value => 6000, :samples => 3}
        handler.should_receive(:send_latency_metric).
            with("nats.latency.1m", latency)
      end
    end
  end

  describe :get_job_args do
    it "should mark the core components" do
      create_fake_collector do |collector, _, _|
        ["CloudController", "DEA", "HealthManager", "Router"].each do |job|
          collector.get_job_tags(job).should == {:role=>"core"}
        end
      end
    end

    it "should mark the service node components" do
      create_fake_collector do |collector, _, _|
        ["MongoaaS-Node", "RMQaaS-Node"].each do |job|
          collector.get_job_tags(job).should ==
              {:role => "service"}
        end
      end
    end

    it "should mark the service provisioner components" do
      create_fake_collector do |collector, _, _|
        [
          "MongoaaS-Provisioner",
          "RMQaaS-Provisioner"
        ].each do |job|
          collector.get_job_tags(job).should ==
              {:role => "service"}
        end
      end
    end
  end

  describe :authorization_headers do
    it "should correctly encode long credentials (no CR/LF)" do
      create_fake_collector do |collector, _, _|
        collector.authorization_headers({:credentials => ["A" * 64, "B" * 64]}).
            should == {
              "Authorization" =>
                 "Basic QUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFB" +
                   "QUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQTpCQkJCQkJC" +
                   "QkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJCQkJC" +
                   "QkJCQkJCQkJCQkJCQkJCQkJCQkJC"}
      end
    end
  end

end
