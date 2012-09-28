source :rubygems

gem "rake"
gem "nats"
gem "em-http-request"
gem "eventmachine", "~> 0.12.11.cloudfoundry.3"
gem "vcap_common", "~> 1.0.3"
gem "vcap_logging"
gem "yajl-ruby"

group :test do
  gem "rspec"

  gem "ci_reporter"

  gem "rcov", :platforms => :ruby_18
  gem "rcov_analyzer", ">= 0.2", :platforms => :ruby_18

  gem "simplecov", :platforms => :ruby_19
  gem "simplecov-clover", :platforms => :ruby_19
  gem "simplecov-rcov", :platforms => :ruby_19
end
