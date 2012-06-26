#!/var/vcap/packages/ruby/bin/ruby --disable-all

require "fileutils"

job_change, hash_change, *updated_packages = ARGV

dea_is_only_changed_package = updated_packages.size == 1 && updated_packages[0] == "dea"
job_and_hash_unchanged = job_change == "job_unchanged" && hash_change == "hash_unchanged"

pidfile = "/var/vcap/sys/run/dea/dea.pid"
default_timeout = 33

if !File.exists?(pidfile)
  puts 0
  exit 0
end

begin
  pid = File.read(pidfile).to_i

  if dea_is_only_changed_package && job_and_hash_unchanged
    Process.kill("KILL", pid)
    FileUtils.rm_rf(pidfile)
    puts 0
  else
    Process.kill("USR2", pid)
    puts (ENV["DEA_DRAIN_TIMEOUT"] || default_timeout).to_i
  end

rescue Errno::ESRCH
  puts 0
end
