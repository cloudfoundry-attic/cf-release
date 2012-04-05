#!/usr/bin/env ruby

require "logger"

require File.expand_path("../gem_cache.rb", __FILE__)

gems_dir       = ARGV[0]
cache_base_dir = ARGV[1]
ruby_base_dir  = ARGV[2]

unless gems_dir && cache_base_dir && ruby_base_dir
  puts "Expected usage: ruby seed_gems.rb gems_dir cache_base_dir ruby_base_dir"
  exit 1
end

cache_dir = File.join(cache_base_dir, "gem_cache")
downloaded_gems_dir = File.join(cache_base_dir, "blessed_gems")

ruby_cmd = File.join(ruby_base_dir, "bin", "ruby")
gem_cmd  = File.join(ruby_base_dir, "bin", "gem")

FileUtils.mkdir_p(cache_dir)
FileUtils.mkdir_p(downloaded_gems_dir)

unless File.file?(ruby_cmd)
  puts "No such file: '#{ruby_cmd}'"
end

cache = GemCache.new(cache_dir)

gems = Dir["#{gems_dir}/*.gem"]

if gems.empty?
  puts "There are no gems in `#{gems_dir}' directory, please make sure dea_transition put them there"
  exit 1
end

gems.each do |gem|
  FileUtils.cp(gem, downloaded_gems_dir)

  Dir.mktmpdir do |gem_install_dir|
    gem_install_output = `#{ruby_cmd} #{gem_cmd} install #{gem} --local --no-rdoc --no-ri -E -w -f --ignore-dependencies --install-dir #{gem_install_dir} 2>&1`
    puts gem_install_output

    if $?.exitstatus != 0
      puts "Failed installing gem: #{$?.exitstatus}"
      exit 1
    end

    if !cache.put(gem, gem_install_dir)
      puts "Failed saving gem to cache"
      exit 1
    end
  end
end
