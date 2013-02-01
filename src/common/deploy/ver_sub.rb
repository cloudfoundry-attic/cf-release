#!/usr/bin/env ruby

manifest = File.read(ARGV[0])
manifest.sub! /(releases:\n- name: appcloud\n  version: ).*\n/, "\\1#{ARGV[1]} # #{ARGV[2]}\n"
open(ARGV[0], 'w') { |f| f.write(manifest) }
