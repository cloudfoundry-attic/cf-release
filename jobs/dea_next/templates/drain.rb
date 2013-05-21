#!/var/vcap/packages/ruby_next/bin/ruby --disable-all

require "logger"
require "fileutils"

logger = Logger.new("/var/vcap/sys/log/dea_next/drain.log")

job_change, hash_change, *updated_packages = ARGV

logger.info("Drain script invoked with #{ARGV.join(" ")}")

dea_only       = (updated_packages == ["dea_next"])
warden_only    = (updated_packages == ["warden"])
dea_and_warden = (updated_packages.sort == ["dea_next", "warden"])

# Must evacuate if job changes, stemcell changes, or a package other than
# the dea or warden changes
need_evacuation = (job_change  != "job_unchanged")  ||
                  (hash_change != "hash_unchanged") ||
                  !(dea_only || warden_only || dea_and_warden)

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
    # XXX: The warden should be rolled after the DEA has exited. Unsure how to
    # make that happen.
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