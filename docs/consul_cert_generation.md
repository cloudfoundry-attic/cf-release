# Generating Certs for Consul

As of
[55c8a19](https://github.com/cloudfoundry/cf-release/commit/55c8a19de1b8a09893010d84034ba9a2b0f5e477),
CF-Release now defaults Consul to requiring SSL encryption for all traffic.

There are a few certs/keys to generate:

1. `ca_cert`: used to verify the keys.
1. `server_cert`, `server_key`: used by the cluster of Consul servers that the
   agents join.
1. `agent_cert`, `agent_key`: used by every client-mode agent colocated across
   your VMs.

In addition, there is a new `encrypt_key` property, which is used to encrypt
data going over Consul's Gossip protocol. This value should be 16 random bytes,
base64-encoded.

To generate the certs, we recommend using
[`certstrap`](https://github.com/square/certstrap). A helper script called
`generate-consul-certs` exists in
[`cf-release/scripts`](https://github.com/cloudfoundry/cf-release/tree/master/scripts)
which will generate everything (including the CA!) for you. If you already have
a CA, you may have an existing workflow, otherwise just modify the script to
use your CA instead of generating one.

The script will place files in `./consul-certs`. You'll need to copy-paste from
these files later on.

To generate `encrypt_key`, just run something like:

```bash
$ cat /dev/urandom | head -c 16 | base64
8b9IJjXH5aN2Z9A5H8HAmg==
```

Once you have your certs and your key, you'll need to provide the values using
a stub that looks something like:

```yaml
properties:
  consul:
    encrypt_key: 8b9IJjXH5aN2Z9A5H8HAmg==
    ca_cert: |
      -----BEGIN CERTIFICATE-----
      ...
      -----END CERTIFICATE-----
    agent_cert: |
      -----BEGIN CERTIFICATE-----
      ...
      -----END CERTIFICATE-----
    agent_key: |
      -----BEGIN RSA PRIVATE KEY-----
      ...
      -----END RSA PRIVATE KEY-----
    server_cert: |
      -----BEGIN CERTIFICATE-----
      ...
      -----END CERTIFICATE-----
    server_key: |
      -----BEGIN RSA PRIVATE KEY-----
      ...
      -----END RSA PRIVATE KEY-----
```

(...or just edit your manifest if you're not using `./generate_deployment_manifest`).

Once you have your stub, you can just delete the generated `./consul-certs`
directory.
