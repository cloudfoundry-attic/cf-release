#!/var/vcap/packages/ruby/bin/ruby --disable-all

require "logger"
require "fileutils"

FileUtils.mkdir_p("/var/vcap/sys/log/dea_next")
logger = Logger.new("/var/vcap/sys/log/dea_next/drain.log")

logger.info("Drain script invoked with #{ARGV.join(" ")}")

dea_pidfile = "/var/vcap/sys/run/dea_next/dea_next.pid"

if !File.exists?(dea_pidfile)
  logger.info("DEA not running")
  puts 0
  exit 0
end

begin
  dea_pid = File.read(dea_pidfile).to_i
  logger.info("Sending signal USR2 to DEA.")
  Process.kill("USR2", dea_pid)
  logger.info("Hey BOSH, call me back in 5s.")
  puts -5
rescue Errno::ESRCH => e
  logger.info("Caught exception: #{e}")
  puts 0
end
