# Each Warden container is a /30 in Warden's network range, which is
# configured as 10.244.0.0/22. There are 256 available entries.
#
# We want two subnets, so I've arbitrarily divided this in half for each.
#
# cf1 will be 10.244.0.0/23
# cf2 will be 10.244.2.0/23
#
# Each network will have 128 subnets, and the first half of each subnet will
# be given static IPs.

require "yaml"
require "netaddr"

cf1_subnets = []
cf1_start = NetAddr::CIDR.create("10.244.0.0/30")

cf2_subnets = []
cf2_start = NetAddr::CIDR.create("10.244.2.0/30")

128.times do
  cf1_subnets << cf1_start
  cf1_start = NetAddr::CIDR.create(cf1_start.next_subnet)

  cf2_subnets << cf2_start
  cf2_start = NetAddr::CIDR.create(cf2_start.next_subnet)
end

puts YAML.dump(
  "networks" => [
    { "name" => "cf1",
      "subnets" => cf1_subnets.collect.with_index do |subnet, idx|
        { "cloud_properties" => {
            "name" => "random",
          },
          "range" => subnet.to_s,
          "reserved" => [subnet[1].ip],
          "static" => idx < 64 ? [subnet[2].ip] : [],
        }
      end
    },
    { "name" => "cf2",
      "subnets" => cf2_subnets.collect.with_index do |subnet, idx|
        { "cloud_properties" => {
            "name" => "random",
          },
          "range" => subnet.to_s,
          "reserved" => [subnet[1].ip],
          "static" => idx < 64 ? [subnet[2].ip] : [],
        }
      end
    },
  ])
