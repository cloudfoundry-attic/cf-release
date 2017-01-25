# Introduction

The `minimal-softlayer.yml` is an example of a minimalistic deployment of Cloud
Foundry, including all crucial components for its basic functionality. It allows
you to deploy Cloud Foundry for educational purposes, so you can poke around and
break things.

The following instructions deploy CF in a completely private network. This has the advantage that no special setup is needed for a Global IP or similar, but comes at the cost of deploying in two steps, which is not the idiomatic use of BOSH.

To make it publicly available, i.e. without a VPN connection, you should set up a Softlayer Global IP or equivalent, configure the haproxy to use it as its public IP.

*IMPORTANT*: This is not meant to be used for a production level deployment as
it doesn't include features such as high availability and security.

# Setup

## Deploy BOSH director in SoftLayer
- https://bosh.io/docs/init-softlayer.html

## Create New Subnet (Portable IPs) For Cloud Foundry Deployment
- To use the portable IPs, you need to first apply them from Softlayer: [utilizing-subnets-and-ips](https://knowledgelayer.softlayer.com/learning/utilizing-subnets-and-ips)

## DNS Configuration

### Using xip.io

If you do *not* have a domain, you can use `0.0.0.0.xip.io` for your System Domain and replace the zeroes with the IP of the haproxy. If the IP is not known yet, because the deployment has not happened yet, just stick with `0.0.0.0.xip.io` as the system domain, replace after the first deployment and deploy again.

- Replace `REPLACE_WITH_SYSTEM_DOMAIN` with `0.0.0.0.xip.io`

**Generate a SSL certificate for your xip.io Domain:**

- Run `openssl genrsa -out cf.key 1024`
- Run `openssl req -new -key cf.key -out cf.csr`
  - For the Common Name, you must enter `*.xip.io`
- Run `openssl x509 -req -in cf.csr -signkey cf.key -out cf.crt`
- Run `cat cf.crt && cat cf.key` and replace `REPLACE_WITH_SSL_CERT_AND_KEY` with this value.

### Using a real domain
This is only needed if you don't use the xip.io domain above.

If you have a domain you plan to use for your Cloud Foundry System Domain. Set up the DNS as follows:
- http://knowledgelayer.softlayer.com/articles/dns-overview-resource-records

Create a wildcard DNS entry for your root System Domain (\*.your-cf-domain.com) to point at the Global IP address that's used by the haproxy.

**Generate a SSL certificate for your System Domain:**

- Run `openssl genrsa -out cf.key 1024`
- Run `openssl req -new -key cf.key -out cf.csr`
  - For the Common Name, you must enter `*.` followed by your System Domain
- Run `openssl x509 -req -in cf.csr -signkey cf.key -out cf.crt`
- Run `cat cf.crt && cat cf.key` and replace `REPLACE_WITH_SSL_CERT_AND_KEY` with this value.

## Replace placeholders

Replace

- `REPLACE_WITH_DIRECTOR_ID` with the director ID as gotten from `bosh status --uuid`
- `REPLACE_WITH_DIRECTOR_IP` with the director IP taken from https://control.softlayer.com
- `REPLACE_WITH_PRIVATE_VLAN_ID` with the ID taken from https://control.softlayer.com/network/vlans/:vlan-id
- `REPLACE_WITH_DATACENTER_NAME` with the datacenter name taken from https://control.softlayer.com
- `REPLACE_WITH_DOMAIN` with an arbitrarily chosen name
- `REPLACE_WITH_VM_PREFIX` with an arbitrarily chosen prefix
- `REPLACE_WITH_PASSWORD` with an arbitrarily chosen password.

## Create and Deploy Cloud Foundry
Download a copy of the latest bosh stemcell:

```sh
bosh public stemcells
bosh download public stemcell light-bosh-stemcell-[GREATEST_NUMBER]-softlayer-xen-ubuntu-trusty-go_agent.tgz

```

Upload the new stemcell:

```sh
bosh upload stemcell light-bosh-stemcell-[GREATEST_NUMBER]-softlayer-xen-ubuntu-trusty-go_agent.tgz
```

Upload the latest stable version of Cloud Foundry.

```sh
bosh upload release https://bosh.io/d/github.com/cloudfoundry/cf-release
bosh upload release https://bosh.io/d/github.com/cloudfoundry/diego-release
bosh upload release https://bosh.io/d/github.com/cloudfoundry/cflinuxfs2-rootfs-release
bosh upload release https://bosh.io/d/github.com/cloudfoundry/garden-runc-release

bosh deployment LOCATION_OF_YOUR_MODIFIED_EXAMPLE_MANIFEST
bosh deploy
```

## Pushing your first application
When interacting with your Cloud Foundry deployment, you will need to download the latest stable version of the
[cf cli](https://github.com/cloudfoundry/cli). You can download a few simple applications with the following commands:

```sh
cd $HOME/workspace
git clone https://github.com/cloudfoundry/cf-acceptance-tests.git
```

For a first application, you can push a light weight ruby application called
[Dora](https://github.com/cloudfoundry/cf-acceptance-tests/tree/master/assets/dora)
which is found at `$HOME/workspace/cf-acceptance-tests/assets/dora`. Lastly you can follow the
[application deploy instructions](http://docs.cloudfoundry.org/devguide/deploy-apps/deploy-app.html) and
push dora into your Cloud Foundry deployment.

## SSH onto your CF Machines
Every once and a while you might want to ssh onto one of your cloud foundry deployment vms and obtain the logs
or perform some other actions. To do this you first need to get a list of vms with the command `bosh vms`. You can then
acccess your machines with the following command:

```sh
bosh ssh VM_FROM_BOSH_VMS_COMMAND/INSTANCE_NUMBER
```

Note that this command will ask you to setup the password for sudo during the login processs.
