# Copyright (c) 2009-2012 VMware, Inc.

module Collector
  # Varz metric handler
  #
  # It's used for processing varz from jobs and publishing them to the TSDB
  # server
  class Handler
    @handler_map = {}

    class << self
      # @return [Hash<String, Handler>] hash of jobs to {Handler}s
      attr_accessor :handler_map

      # Registers the {Handler} for a job type.
      #
      # @param [String, Symbol] job the job name
      def register(job)
        job = job.to_s
        Config.logger.info("Registering handler: #{self} for job: #{job}")
        raise "Job: #{job} already registered" if Handler.handler_map[job]
        Handler.handler_map[job] = self
      end

      # Retrieves a {Handler} for the job type with the provided context. Will
      # default to the generic one if the job does not have a handler
      # registered.
      #
      # @param [TsdbConnection] tsdb_connection the TSDB connection to use for
      #   writing metrics
      # @param [String] job the job name
      # @param [Fixnum] index the job index
      # @param [Fixnum] now the timestamp of when the metrics were collected
      # @return [Handler] the handler for this job from the handler map or the
      #   default one
      def handler(tsdb_connection, job, index, now)
        if handler_class = Handler.handler_map[job]
          handler_class.new(tsdb_connection, job, index, now)
        else
          Handler.new(tsdb_connection, job, index, now)
        end
      end
    end

    # @return [String] job name
    attr_accessor :job

    # @return [Fixnum] job index
    attr_accessor :index

    # @return [Fixnum] timestamp when metrics were collected
    attr_accessor :now

    # Creates a new varz handler
    #
    # @param [Collector::TsdbConnection] tsdb_connection
    # @param [String] job the job for this varz
    # @param [Fixnum] index the index for this varz
    # @param [Fixnum] now the timestamp when it was collected
    def initialize(tsdb_connection, job, index, now)
      @tsdb_connection = tsdb_connection
      @job = job
      @index = index
      @now = now
      @logger = Config.logger
    end

    # Processes varz in the context of the collection
    #
    # @param [Hash] varz the varzs collected for this job
    def process(varz)
    end

    # Sends the metric to the TSDB server
    #
    # @param [String] name the metric name
    # @param [String, Fixnum] value the metric value
    # @param [Hash] tags the metric tags
    def send_metric(name, value, tags = {})
      tags = tags.merge({:job => @job, :index => @index}).
                  collect { |tag| tag.join("=") }.sort.join(" ")
      command = "put #{name} #{@now} #{value} #{tags}\n"
      @logger.debug1(command)
      @tsdb_connection.send_data(command)
    end

    # Sends latency metrics to the TSDB server
    #
    # @param [String] name the metric name
    # @param [Hash] value the latency metric value
    # @param [Hash] tags the metric tags
    def send_latency_metric(name, value, tags = {})
      if value && value["samples"] && value["samples"] > 0
        send_metric(name, value["value"] / value["samples"], tags)
      end
    end
  end
end