## Introduction

The last several months have seen the addition
of a large security feature
whereby communication between CF components uses mutual TLS for encryption.
The unfortunate side-effect is that
certificate management has become a complex aspect of the operator experience.
To reduce confusion about how to generate and sign various certificates,
we're providing this doc that hopes
to explain the relationship between certificates and their certificate authorities,
and
to give a step-by-step guide to generating these certificates.

## Table of contents
- <a href='#cert-architecture'>An explanation of the certificate architecture</a>
- <a href='#guide'>Step-by-step guide</a>

## <a name='cert-architecture'></a> The Certificate Architecture
In order for one component to trust that an SSL certificate is valid,
it validates that the certificate was signed by a trusted _Certificate Authority_ (CA).
A CA is itself represented by an SSL certificate/key pair.
When an operator generates a new certificate,
they choose a CA and use its private key to sign the certificate.
Any component that trusts that CA will also trust the new certificate.

Choosing a CA configuration for a component depends heavily
on the granularity of the configuration allowed by the component.
The result is typically
some CAs that sign certificates for a "clique" of components,
and some CAs that sign certificates for a wide-reaching set of components.  

### An example of a "clique" CA
Take the `etcd` cluster and its clients as an example.
`etcd` contains the following interactions:
- `etcd` nodes communicate with one another as peers to establish quorum
- `doppler` is a client of the `etcd` cluster
- `loggregator_trafficcontroller` is a client of the `etcd` cluster

The interaction between the etcd nodes is encrypted with the "etcd peer" certificate/key pair.
The interaction between the clients
(`doppler`, and `loggregator_trafficcontroller`)
and the etcd cluster use mutual TLS with the "etcd server" and "etcd client" certificate/key pairs.

The `etcd` jobs allows the deployer to provide two CA certificates:
[one](https://github.com/cloudfoundry-incubator/etcd-release/blob/v99/jobs/etcd/spec#L70) for the peer interaction,
and [one](https://github.com/cloudfoundry-incubator/etcd-release/blob/v99/jobs/etcd/spec#L47) for the client/server interaction.
So an overzelous deployer could choose to sign the "etcd peer" certificates with one CA, and the "etcd server" and "etcd client" certificates with another CA.
A lazy deployer, on the other hand, may choose to sign them both with the same CA.

### An example of a widely-shared CA
On the other end of the spectrum,
consider the Cloud Controller,
which only allows for a [single CA](https://github.com/cloudfoundry/cloud_controller_ng/blob/47e1c03d16978acd01bbc9d7bef203474dcd7afa/bosh/jobs/cloud_controller_ng/spec#L814)
to be provided for mutual TLS.

The Cloud Controller has (among many others) the following interactions:
- `cc_bridge` is resource for which the Cloud Controller is a client
- Diego's `bbs` is resource for which the Cloud Controller is a client
- `syslog_drain_binder` is a client of the Cloud Controller

As a result,
the `cc_bridge`, `bbs`, and `syslog_drain_binder`
must use that specific CA
to sign certificates when communicating the the Cloud Controller.
That CA is called the `cf-diego-ca` in our certificate generation scripts,
because it signs certificates used in CF <-> Diego communication.

Note that,
even if you aren't deploying Diego yet,
the `cf-diego-ca` is still required for signing certificates
used by the Cloud Controller's interactions with other components.

## <a name='guide'></a>Step-by-step guide to generating certificates

### Start by generating certs for the small "cliques"

#### Consul
Generate the certs and keys:
```
./scripts/generate-consul-certs
```

The script creates a CA for consul,
generates a keypair for both the agents and servers,
and signs the them with the CA.
Place the following generated values in the your stub or manifest:

| Script output | Properties |
| ------------- | -------- |
| consul-certs/server-ca.crt | `properties.consul.ca_cert` |
| consul-certs/server.crt | `properties.consul.server_cert` |
| consul-certs/server.key | `properties.consul.server_key` |
| consul-certs/agent.crt | `properties.consul.agent_cert` |
| consul-certs/agent.key | `properties.consul.agent_key` |

#### Etcd
Generate the certs and keys:
```
./scripts/generate-etcd-certs
```

The script creates two CAs,
one for client/server interactions
and another for internal peer interaction.
It uses those CAs to sign the three keypairs it generates.

| Script output | Properties |
| ------------- | -------- |
| etcd-certs/etcd-ca.crt | <ul><li>`properties.etcd.ca_cert`</li><li>`properties.loggregator.etcd.ca_cert`</li></ul> |
| etcd-certs/server.crt | `properties.etcd.server_cert` |
| etcd-certs/server.key | `properties.etcd.server_key` |
| etcd-certs/client.crt | <ul><li>`properties.etcd.client_cert`</li><li>`properties.doppler.etcd.client_cert`</li><li>`properties.traffic_controller.etcd.client_cert`</li><li>`properties.syslog_drain_binder.etcd.client_cert`</li></uls> |
| etcd-certs/client.key | <ul><li>`properties.etcd.client_key`</li><li>`properties.doppler.etcd.client_key`</li><li>`properties.traffic_controller.etcd.client_key`</li><li>`properties.syslog_drain_binder.etcd.client_key`</li></uls> |
| etcd-certs/peer-ca.crt | `properties.etcd.peer_ca_cert` |
| etcd-certs/peer.crt | `properties.etcd.peer_cert` |
| etcd-certs/peer.key | `properties.etcd.peer_key` |

#### Blobstore (if you're deploying your own)
Generate the certs and keys:
```
./scripts/generate-blobstore-certs
```

The script generates a CA and a certificate for the WebDAV blobstore.

| Script Output | Properties |
| ------------- | ---------- |
| blobstore-certs/server-ca.crt | `properties.blobstore.tls.ca_cert` |
| blobstore-certs/server.crt | `properties.blobstore.tls.cert` |
| blobstore-certs/server.key | `properties.blobstore.tls.private_key` |

#### UAA
Generate the certs and keys:
```
./scripts/generate-uaa-certs
```

The script generates a CA and sert for the UAA.

| Script Output | Properties |
| ------------- | ---------- |
| uaa-certs/server-ca.crt | `properties.uaa.ca_cert` |
| uaa-certs/server.crt | `properties.uaa.sslCertificate` |
| uaa-certs/server.key | `properties.uaa.sslPrivateKey` |

### The interrelated components

#### First, generate the `cf-diego-ca`
Generate the certs and keys:
```
./scripts/generate-cf-diego-certs
```

The script generates mutual TLS certs for the Cloud Controller,
as well as the `cf-diego-ca` that will be used to sign other certificates.

| Script Output | Properties |
| ------------- | -------- |
| cf-diego-certs/cf-diego-ca.crt | <ul> <li>`properties.cc.mutual_tls.ca_cert`</li> <li>`properties.capi.tps.cc.ca_cert` in the Diego manifest</li></ul> |
| cf-diego-certs/cloud_controller.crt | `properties.cc.mutual_tls.public_cert` |
| cf-diego-certs/cloud_controller.key | `properties.cc.mutual_tls.private_key` |

#### Next, generate the certificates for Diego
Generate the certs and keys using the script in
[diego-release](https://github.com/cloudfoundry/diego-release).
These certs must be signed by `cf-diego-ca`:
```
../diego-release/scripts/generate-diego-certs ./cf-diego-certs
```


| Script Output | Properties |
| ------------- | ---------- |
| diego-certs/client.key | `properties.capi.tps.cc.client_key` |
| diego-certs/client.crt | `properties.capi.tps.cc.client_cert` |

#### Generate the certificates for Loggregator
Generate the certificates and keys:
```
./scripts/generate-loggregator-certs cf-diego-certs/cf-diego-ca.crt cf-diego-certs/cf-diego-ca.key
```

This certificates creates certificates
for traffic controller, doppler, metron, and syslog_drain_binder.
The first three certificates are signed by a newly-generated loggregatorCA,
and the syslog_drain_binder is signed by the `cf-diego-ca`.

| Script Output | Properties |
| ------------- | ---------- |
| loggregator-certs/loggregator-ca.crt | `properties.loggregator.tls.ca_cert` |
| loggregator-certs/doppler.crt | `properties.loggregator.tls.doppler.cert` |
| loggregator-certs/doppler.key | `properties.loggregator.tls.doppler.key` |
| loggregator-certs/metron.crt | `properties.loggregator.tls.metron.cert` |
| loggregator-certs/metron.key | `properties.loggregator.tls.metron.key` |
| loggregator-certs/trafficcontroller.crt | `properties.loggregator.tls.trafficcontroller.cert` |
| loggregator-certs/trafficcontroller.key | `properties.loggregator.tls.trafficcontroller.key` |
| loggregator-certs/syslogdrainbinder.crt | `properties.loggregator.tls.syslogdrainbinder.cert` |
| loggregator-certs/syslogdrainbinder.key | `properties.loggregator.tls.syslogdrainbinder.key` |

#### Last, generate the certs for statsd-injector
Generate the certificates and keys.
These certs must be signed by the loggregator CA:
```
./scripts/generate-statsd-injector-certs loggregator-certs/loggregator-ca.crt loggregator-certs/loggregator-ca.key
```

| Script Output | Properties |
| ------------- | ---------- |
| statsd-injector-certs/doppler.crt | `properties.loggregator.tls.statsd_injector.cert` |
| statsd-injector-certs/doppler.key | `properties.loggregator.tls.statsd_injector.key` |
