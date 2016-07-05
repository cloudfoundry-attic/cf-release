# Introduction

The `minimal-aws.yml` is an example of a minimalistic deployment of Cloud
Foundry, including all crucial components for its basic functionality. it allows
you to deploy Cloud Foundry for educational purposes, so you can poke around and
break things. 

*IMPORTANT*: This is not meant to be used for a production level deployment as
it doesn't include features such as high availability and security.

# Setup

## Deploy BOSH director in AWS
- https://bosh.io/docs/init-aws.html

##Update the BOSH Security Group to allow "cf logs" to work
- Click on "VPC" from the Amazon Web Services Dashboard
- Click on "Security Groups" from the VPC Dashboard
- Select the "bosh" Security Group
- Click "Inbound Rules" at the bottom
- Click "Edit"
- Fill in a new rule
  - Type: Custom TCP Rule
  - Protocol: TCP (6)
  - Port Range: 4443
  - Source: 0.0.0.0/0
- Click "Save"

- Replace REPLACE_WITH_DIRECTOR_ID in the example manifest with the bosh director id (return by running "bosh status --uuid")
- Replace REPLACE_WITH_BOSH_SECURITY_GROUP in the example manifest with the security group created for BOSH

## Create a NAT Machine in the bosh subnet
- Click on "EC2" from the Amazon Web Services Dashboard
- Click "Launch Instance"
- Click "Community AMIs"
- Search for "amzn-ami-vpc-nat"
- Select "amzn-ami-vpc-nat-pv-2014.09.1.x86_64-ebs"
- Select "m1.small"
- Click "Next: Configure Instance Details"
- Fill in
  - Network: bosh
  - Subnet: bosh
  - Auto-assign Public IP: Enable
- Click "Next: Add Storage"
  - If asked to choose boot volume for instance, select the "Continue with Magnetic..." option.
- Click "Next: Tag Instance"
- Enter "NAT" for the Name value
- Click "Next: Configure Security Group"
- Click "Create a new security group"
- Fill in
  - Security group name: nat
  - Description: NAT Security Group
  - Type: All traffic
  - Protocol: All
  - Port Range: 0 - 65535
  - Source: Custom IP / 10.0.16.0/24
- Click "Review and Launch"
- Click "Launch"
- Use the existing bosh key pair
- Click "Launch Instances"
- Click "View Instances"
- Select the NAT instance in the Instances list
- Click "Actions" => "Networking" => "Change Source/Dest. Check."
- Click "Yes, Disable"

## Create New Subnet For Cloud Foundry Deployment
- Click on "Subnets" from the VPC Dashboard
- Click "Create Subnet"
- Fill in
  - Name tag: cf
  - VPC: bosh
  - Availability Zone: Pick the same Availability Zone as the bosh Subnet
    - Replace REPLACE_WITH_AZ in the example manifest with the Availability Zone you chose
  - CIDR block: 10.0.16.0/24
  - Click "Yes, Create"
- Replace REPLACE_WITH_PRIVATE_SUBNET_ID in the example manifest with the Subnet ID for the cf Subnet
- Replace REPLACE_WITH_PUBLIC_SUBNET_ID in the example manifest with the Subnet ID for the bosh Subnet
- Select the cf Subnet from the Subnet list
- Click the name of the "Route table:" to view the route tables
- Select the route table from the list
- Click "Routes" in the bottom window
- Click "Edit"
- Fill in a new route
  - Distination: 0.0.0.0/0
  - Target: Select the NAT instance from the list
- Click "Save"

- Click on "Elastic IPs" from the VPC Dashboard
- Click "Allocate New Address"
- Replace REPLACE_WITH_ELASTIC_IP in the example manifest with the new IP address

- Click on "Security Groups" from the VPC Dashboard
- Click "Create Security Group"
- Name: cf-public
- Description: cf-public
- VPC: Select the bosh VPC
- Add in the follwing inbound rules:

  | Type  | Protocol | Port Range | Source   |
  |-------|----------|------------|----------|
  | HTTP  | TCP      | 80         | Anywhere |
  | HTTPS | TCP      | 443        | Anywhere |
  | TCP   | TCP      | 4443       | Anywhere |

- Replace REPLACE_WITH_PUBLIC_SECURITY_GROUP in the example manifest with the new security group

## DNS Configuration
If you have a domain you plan to use for your Cloud Foundry System Domain. Set up the DNS as follows:

Create a wildcard DNS entry for your root System Domain (\*.your-cf-domain.com) to point at the above Elastic IP address

- Click on "Route 53" from the Amazon Web Services Dashboard
- Click on "Hosted Zones"
- Select your zone
- Click "Go to Record Sets"
- Click "Create Record Set"
- Fill in
  - Name: *
  - Type: A - IPv4 address
  - Value: The Elastic IP create above
  - Click "Create"

If you do NOT have a domain, you can use 0.0.0.0.xip.io for your System Domain and replace the zeroes with your Elastic IP

- Replace REPLACE_WITH_SYSTEM_DOMAIN with your system domain

Generate a SSL certificate for your System Domain

- Run "openssl genrsa -out cf.key 1024"
- Run "openssl req -new -key cf.key -out cf.csr"
  - For the Common Name, you must enter "\*." followed by your System Domain
- Run "openssl x509 -req -in cf.csr -signkey cf.key -out cf.crt"
- Run "cat cf.crt && cat cf.key" and replace REPLACE_WITH_SSL_CERT_AND_KEY with this value.

## Create and Deploy CF Release
Download a copy of the latest bosh stemcell.

```sh
bosh public stemcells
bosh download public stemcell light-bosh-stemcell-[GREATEST_NUMBER]-aws-xen-hvm-ubuntu-trusty-go_agent.tgz

```

Upload the new stemcell.

```sh
bosh upload stemcell light-bosh-stemcell-[GREATEST_NUMBER]-aws-xen-hvm-ubuntu-trusty-go_agent.tgz
```

Replace REPLACE_WITH_BOSH_STEMCELL_VERSION in the example manifest with the BOSH stemcell version you downloaded above

Upload the latest stable version of Cloud Foundry.

```sh
git clone https://github.com/cloudfoundry/cf-release.git
cd cf-release

bosh upload release releases/cf-GREATEST_NUMBER.yml

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
bosh ssh VM_FROM_BOSH_VMS_COMMAND/INSTANCE_NUMBER --gateway_host YOUR_PUBLIC_BOSH_ADDRESS --gateway_user vcap
```

Note that this command will ask you to setup the password for sudo during the login processs.

