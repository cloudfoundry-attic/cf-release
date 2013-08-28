#!/usr/bin/env ruby

require 'syslog'
require 'nats/client'

@syslog = Syslog.open('vcap.nats', Syslog::LOG_PID, Syslog::LOG_USER)

%w[TERM INT].each do |sig|
  trap(sig) do
    NATS.stop
    exit(0)
  end
end

def log(level, timestamp, message, data)
  syslog_level = level == :error ? Syslog::LOG_ERR : Syslog::LOG_INFO
  @syslog.log(syslog_level, %Q[{"timestamp":%f,"source":"NatsStreamForwarder","log_level":%s,"message":%s,"data":%s}],
            timestamp, level.to_s.inspect, message.inspect, data)
end

def json_safe(string)
  string.nil? ? 'null' : string.inspect
end

begin
  NATS.start(:uri => ARGV[0]) do
    NATS.subscribe(">") do |message, reply_inbox, subject|
      received_at = Time.now.to_f
      EM.defer do
        begin
          json = %Q[{"nats_message": #{json_safe message},"reply_inbox":#{json_safe reply_inbox}}]
          log(:info, received_at, subject, json)
        rescue Exception => e
          puts "Error logging to syslog #{e.inspect}\n  #{e.backtrace.join("\n  ")}"
        end
      end
    end
  end
rescue NATS::ConnectError, NATS::ServerError => e
  log(:error, Time.now.to_f, "nats.error", %Q[{"exception_message": #{json_safe e.message}}])
  retry
end
