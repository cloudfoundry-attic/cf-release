#!/var/vcap/packages/ruby_next/bin/ruby --disable-all

require "logger"
require "fileutils"

logger = Logger.new("/var/vcap/sys/log/dea_next/drain.log")

job_change, hash_change, *updated_packages = ARGV

logger.info("Drain script invoked with #{ARGV.join(" ")}")

# Must evacuate if packages other than the DEA were updated.
#
# The DEA itself can be kill -9'd, and apps won't go down. Warden is what's
# actually running them, so if its package is changed, we need to evacuate.
#
# In general, if any package other than the DEA is updated, we'll want to
# evacuate and start fresh. For example, if we upgrade Ruby.
need_evacuation = updated_packages != ["dea_next"]

logger.info("Need evacuation? #{need_evacuation}")

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

  if need_evacuation
    logger.info("Sending signal USR2 to DEA.")
    Process.kill("USR2", dea_pid)
    timeout = (ENV["DEA_DRAIN_TIMEOUT"] || default_timeout).to_i
    logger.info("Setting timeout as #{timeout}.")
    puts timeout
    # XXX: Warden should be rolled after the DEA has exited. Unsure how to make
    # that happen.
  else
    logger.info("Sending signal KILL to DEA.")

    Process.kill("KILL", dea_pid)
    FileUtils.rm_f(dea_pidfile)

    sleep 0.5

    # Persist container state so the DEA can pick up the containers
    logger.info("Sending signal USR2 to Warden.")
    Process.kill("USR2", warden_pid)
    FileUtils.rm_f(warden_pidfile)

    # Give the warden a bit of time to write out its state
    puts 1
  end

rescue Errno::ESRCH => e
  logger.info("Caught exception: #{e}")
  puts 0
end
