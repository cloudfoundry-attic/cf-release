# Runtime configs

For a general primer on runtime-configs, see the documentation on [bosh.io](https://bosh.io/docs/runtime-config.html).
Briefly, a runtime-config allows operators to configure jobs to be colocated on _every_ instance group.

**List of runtime-configs**

| File | Usage |
|------|-------|
| `syslog-forwarder.yml` | This collocates a BOSH job from [syslog release](https://github.com/cloudfoundry/syslog-release) to forward local syslog events in RFC5424 format to a remote syslog endpoint. It currently uses rsyslog which is pre-installed by the stemcell.  |

## Deploying with the runtime-config
After uploading a runtime-config to your Bosh director
(see below for instructions on uploading runtime-configs),
you must re-deploy any `bosh` deployments to pick up the runtime config change.

## `syslog-forwarder.yml`

We have provided a `syslog-forwarder.yml` runtime-config to make it easy to collocate syslog-forwarder for forwarding syslogs to a remote syslog endpoint.
The syslog-forwarder job requires configuration of the following values:

| Spec property | bosh variable |
|---------------|---------------|
| `syslog.address:` | `((address))` |
| `syslog.port:` | `((port))` |
| `syslog.transport:` | `((transport))` |
| `syslog.fallback_servers:` | `((fallback_servers))` |

You can find more information for these variables
in the [syslog-forwarder job spec](https://github.com/cloudfoundry/syslog-release/blob/master/jobs/syslog_forwarder/spec):

```
  syslog.address:
    description: IP or DNS address of the syslog server.
    example: logs4.papertrail.com
  syslog.port:
    description: Port of the syslog server.
    default: 514
  syslog.transport:
    default: tcp
    description: One of `udp`, `tcp`, `relp`.
  syslog.fallback_servers:
    description: "List of fallback servers to be used if the primary syslog server is down. Only tcp or relp protocols are supported. Each list entry should consist of \"address\", \"transport\" and \"port\" keys."
    default: []
    example:
    - address: logs5.papertrail.com
      port: 44312
      transport: tcp
```

### Uploading the `syslog-forwader.yml` runtime-config

The workflow for uploading the runtime-config varies
depending on which BOSH CLI you prefer to use.
If you are using the ruby bosh cli (version < 2.0),
you will need to edit the runtime-config file in place
to reflect the syslog-forwarder configuration.
If you are using the go bosh-cli (version > 2.0),
you can configure the syslog-forwarder job using variables on the command line.

#### Bosh CLI v1 (Ruby CLI) workflow

The files in the `runtime-configs` folder use `bosh` variables to configure themselves.  `bosh` variables look like strings wrapped in double-parenthesis, like `((address))`.  If you are using the ruby `bosh` cli then it will not allow you to configure these variables on the command line.  Instead, you will need to configure them by manually editing the file to provide the required values. After editing this file, you will need to set the runtime config on your `bosh` director:
```
bosh -t TARGET update runtime-config cf-release/runtime-configs/syslog-forwarder.yml
```

Then, you must re-deploy any `bosh` deployments to pick up the runtime config change.

#### Bosh CLI v2 (Go CLI) workflow

The `bosh` cli v2 will allow you to configure the runtime-config addons when you upload them to your `bosh` director.  You can configure and upload the runtime-configs in a single command like this:

```
bosh -e ENV update-runtime-config cf-release/runtime-configs/syslog-forwarder.yml \
-v address=papertrail-url \
-v port=514 \
-v transport=tcp \
-v fallback_servers="[{address: other-url, port: 515, transport: udp}]" \
-v custom_rule='""'
```

Each use of `-v` sets the value of a single variable that will be replaced in the resulting manifest.  You can use YAML syntax in passing variables to set complex data structures, as the example above does for the `fallback_servers` variable.
