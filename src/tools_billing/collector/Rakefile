# Copyright (c) 2009-2012 VMware, Inc.

$:.unshift(File.expand_path("../../rake", __FILE__))

ENV["BUNDLE_GEMFILE"] ||= File.expand_path("../Gemfile", __FILE__)

require "rubygems"
require "bundler"
Bundler.setup(:default, :test)

require "rake"
require "rake/dsl_definition"
begin
  require "rspec/core/rake_task"
rescue
end

require "bundler_task"
require "ci_task"

BundlerTask.new

if defined?(RSpec)
  task :default => :spec

  desc "Run all tests"
  task "spec" => "spec:unit"

  namespace "spec" do
    SPEC_OPTS = %w(--format progress --color)

    desc "Run unit tests"
    unit_rspec_task = RSpec::Core::RakeTask.new(:unit) do |t|
      t.pattern = "spec/unit/**/*_spec.rb"
      t.rspec_opts = SPEC_OPTS
    end

    CiTask.new do |task|
      task.rspec_task = unit_rspec_task
    end

  end
end
