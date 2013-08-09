#!/var/vcap/packages/ruby/bin/ruby --disable-all

require "logger"
require "fileutils"

FileUtils.mkdir_p("/var/vcap/sys/log/dea_next")
logger = Logger.new("/var/vcap/sys/log/dea_next/drain.log")

job_change, hash_change, *updated_packages = ARGV

logger.info("Drain script invoked with #{ARGV.join(" ")}")

dea_pidfile = "/var/vcap/sys/run/dea_next/dea_next.pid"
warden_pidfile = "/var/vcap/sys/run/warden/warden.pid"

# give the DEAS a while to evacuate and restart apps
default_timeout = 115

if !File.exists?(dea_pidfile)
  logger.info("DEA not running")
  puts 0
  exit 0
end

begin
  dea_pid = File.read(dea_pidfile).to_i
  warden_pid = File.read(warden_pidfile).to_i

  logger.info("Sending signal USR1 to DEA.")
  Process.kill("USR2", dea_pid)
  timeout = (ENV["DEA_DRAIN_TIMEOUT"] || default_timeout).to_i
  logger.info("Setting timeout as #{timeout}.")
  puts timeout
  # XXX: Warden should be rolled after the DEA has exited. Unsure how to make
  # that happen.

rescue Errno::ESRCH => e
  logger.info("Caught exception: #{e}")
  puts 0
end
