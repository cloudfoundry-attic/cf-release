# Each Warden container gets a network with this layout:
#
#   10.244.0.(n + 0): container host broadcast IP
#   10.244.0.(n + 1): container host IP
#   10.244.0.(n + 2): container IP that we care about in the CF network
#   10.244.0.(n + 3): container broadcast IP
#
# Thus, we want to reserved (n + 0) and (n + 3) for everything in the range,
# and allocate a few static IPs for (n + 1).

require "yaml"

base = "10.244.0"

cf1_host_networks = (128 / 4).times.collect { |x| x * 4 }
cf2_host_networks = (128 / 4).times.collect { |x| 128 + x * 4 }

cf1_container_host_ips = cf1_host_networks.collect { |x| x + 1 }
cf2_container_host_ips = cf2_host_networks.collect { |x| x + 1 }

cf1_container_ips = cf1_host_networks.collect { |x| x + 2 }
cf2_container_ips = cf2_host_networks.collect { |x| x + 2 }

cf1_container_bcasts = cf1_host_networks.collect { |x| x + 3 }
cf2_container_bcasts = cf2_host_networks.collect { |x| x + 3 }

cf1_reserved =
  (
    cf1_host_networks +
    cf1_container_host_ips +
    cf1_container_bcasts
  ).sort.collect { |x| "#{base}.#{x}" }

cf2_reserved =
  (
    cf2_host_networks +
    cf2_container_host_ips +
    cf2_container_bcasts
  ).sort.collect { |x| "#{base}.#{x}" }

cf1_static_ip_count = cf1_host_networks.size / 4
cf2_static_ip_count = cf2_host_networks.size / 4

cf1_static = cf1_container_ips[0...cf1_static_ip_count].collect { |x| "#{base}.#{x}" }
cf2_static = cf2_container_ips[0...cf2_static_ip_count].collect { |x| "#{base}.#{x}" }

puts YAML.dump(
  "networks" => [
    { "name" => "cf1",
      "subnets" => [
        { "cloud_properties" => {
            "name" => "random",
          },
          "range" => "#{base}.0/24",
          "reserved" => ["#{base}.128 - #{base}.254"] + cf1_reserved[1..-1], # 0 is out of range
          "static" => cf1_static,
        },
      ],
    },
    { "name" => "cf2",
      "subnets" => [
        { "cloud_properties" => {
            "name" => "random",
          },
          "range" => "#{base}.0/24",
          "reserved" => ["#{base}.1 - #{base}.127"] + cf2_reserved[0..-2], # 255 is out of range
          "static" => cf2_static,
        },
      ],
    },
  ])
