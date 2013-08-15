#!/usr/bin/env ruby

require 'syslog'
require 'nats/client'

syslog = Syslog.open('vcap.nats', Syslog::LOG_PID, Syslog::LOG_USER)

%w[TERM INT].each do |sig|
  trap(sig) do
    NATS.stop
  end
end

NATS.start(:uri => ARGV[0]) do
  NATS.subscribe(">") do |message, _, subject|
    begin
      json = %Q[{"timestamp": #{Time.now.to_f},"subject": #{subject.inspect},"message": #{message.nil? ? 'null' : message.inspect}}]
      syslog.log(Syslog::LOG_INFO, "%s", json)
    rescue Exception => e
      puts "Error logging to syslog #{e.inspect}\n  #{e.backtrace.join("\n  ")}"
    end
  end
end
