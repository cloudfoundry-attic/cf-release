## Introduction

The last several months have seen the addition
of a large security feature
whereby communication between CF components uses mutual TLS for encryption.
The unfortunate side-effect is that
certificate management has become a complex aspect of the operator experience.
To reduce confusion about how to generate and sign various certificates,
we're providing this doc that hopes
to give a step-by-step guide to generating these certificates,
and
to explain the relationship between certificates and their certificate authorities.

## Table of contents
- Step-by-step guide
- <a href='#cert-architecture'>An explanation of the certificate architecture</a>

## Step-by-step guide to generating certificates


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
