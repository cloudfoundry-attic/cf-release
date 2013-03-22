#!/var/vcap/packages/ruby_next/bin/ruby --disable-all

require "fileutils"

job_change, hash_change, *updated_packages = ARGV

dea_only       = (updated_packages == ["dea_next"])
warden_only    = (updated_packages == ["warden"])
dea_and_warden = (updated_packages.sort == ["dea_next", "warden"])

# Must evacuate if job changes, stemcell changes, or a package other than
# the dea or warden changes
need_evacuation = (job_change  != "job_unchanged")  ||
                  (hash_change != "hash_unchanged") ||
                  !(dea_only || warden_only || dea_and_warden)

dea_pidfile = "/var/vcap/sys/run/dea_next/dea_next.pid"
warden_pidfile = "/var/vcap/sys/run/warden/warden.pid"

default_timeout = 33

if !File.exists?(dea_pidfile)
  puts 0
  exit 0
end

begin
  dea_pid = File.read(dea_pidfile).to_i
  warden_pid = File.read(warden_pidfile).to_i

  if need_evacuation
    Process.kill("USR2", dea_pid)
    puts (ENV["DEA_DRAIN_TIMEOUT"] || default_timeout).to_i
    # XXX: The warden should be rolled after the DEA has exited. Unsure how to
    # make that happen.
  else
    Process.kill("KILL", dea_pid)
    FileUtils.rm_f(dea_pidfile)

    sleep 0.5

    # Persist container state so the DEA can pick up the containers
    Process.kill("USR2", warden_pid)
    FileUtils.rm_f(warden_pidfile)

    # Give the warden a bit of time to write out its state
    puts 1
  end

rescue Errno::ESRCH
  puts 0
end
