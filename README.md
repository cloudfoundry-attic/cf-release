[![Build Status](https://travis-ci.org/cloudfoundry/cf-release.svg?branch=develop)](https://travis-ci.org/cloudfoundry/cf-release)

# Welcome to Cloud Foundry

Cloud Foundry is an open platform as a service (PaaS) that provides a choice of clouds, developer frameworks, and application services. Cloud Foundry makes it faster and easier to build, test, deploy, and scale applications.

This repository contains the Cloud Foundry source code.

Our documentation (currently a work in progress) is available here: [http://docs.cloudfoundry.org/](http://docs.cloudfoundry.org/).

## About Branches

The [**develop**](https://github.com/cloudfoundry/cf-release/tree/develop) branch is where we do active development. Although we endeavor to keep the [**develop**](https://github.com/cloudfoundry/cf-release/tree/develop) branch stable, we do not guarantee that any given commit will deploy cleanly.

The [**release-candidate**](https://github.com/cloudfoundry/cf-release/tree/release-candidate) branch has passed all of our unit, integration, smoke, & acceptance tests, but has not been used in a final release yet. This branch should be fairly stable.

The [**master**](https://github.com/cloudfoundry/cf-release/tree/master) branch points to the most recent stable final release.

At semi-regular intervals a final release is created from the [**release-candidate**](https://github.com/cloudfoundry/cf-release/tree/release-candidate) branch. This final release is tagged and pushed to the [**master**](https://github.com/cloudfoundry/cf-release/tree/master) branch.

Pushing to any branch other than [**develop**](https://github.com/cloudfoundry/cf-release/tree/develop) will create problems for the CI pipeline, which relies on fast forward merges. To recover from this condition follow the instructions [here](docs/fix_commit_to_master.md).

## Repository Contents

This repository is structured for use with [BOSH](http://github.com/cloudfoundry/bosh); an open source tool for release engineering, deployment and lifecycle management of large scale distributed services. 
There are two directories of note:

Source:

- **jobs**: start and stop commands for each of the jobs (processes) running on Cloud Foundry nodes.
- **packages**: packaging instructions used by BOSH to build each of the dependencies.
- **src**: the source code for the components in Cloud Foundry. Note that each of the components is a submodule with a pointer to a specific SHA.

Releases:

- **releases**: yml files containing the references to blobs for each package in a given release; these are solved within **.final_builds**
- **.final_builds**: references into the public blostore for final jobs & packages (each referenced by one or more **releases**)
- **config**: URLs and access credentials to the bosh blobstore for storing final releases
- **git**: Local git hooks

See the [documentation for deploying Cloud Foundry](http://docs.cloudfoundry.org/deploying/) for more information about using BOSH.

In order to deploy Cloud Foundry with BOSH, you will need to create a manifest.
To do so, ensure that you have installed [Spiff](https://github.com/cloudfoundry-incubator/spiff) before running `./generate_deployment_manifest <infrastructure-type>`; where `<infrastructure-type>` is one of `aws`, `vsphere`, or `warden`.
This script merges together several manifest stubs from the templates directory using Spiff. Consult the [spiff repository](https://github.com/cloudfoundry-incubator/spiff) for more information on installing and using spiff.

A complete [sample manifest for vSphere](http://docs.cloudfoundry.org/deploying/vsphere/cloud-foundry-example-manifest.html) is also available in the Cloud Foundry documentation.

## Cloud Foundry Components (V2)

The current development efforts center on V2, also known as NG. For information on what the core team is working on, please see [our roadmap](http://github.com/cloudfoundry-community/cf-docs-contrib/wiki#roadmap-and-trackers).

The components in a V2 deployment are:

| Component                                                                     | Description                                                                                                                                                         | Build Status                                                                                                                                                 |
|-------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Cloud Controller (cc)](http://github.com/cloudfoundry/cloud_controller_ng) | The primary API entry point for Cloud Foundry.                                                                                                                      |<a href="https://travis-ci.org/cloudfoundry/cloud_controller_ng"><img src="https://travis-ci.org/cloudfoundry/cloud_controller_ng.png" alt="Build Status"></a>|
| [gorouter](https://github.com/cloudfoundry/gorouter)                          | The central router that manages traffic to applications deployed on Cloud Foundry.                                                                                  |<a href="https://travis-ci.org/cloudfoundry/gorouter"><img src="https://travis-ci.org/cloudfoundry/gorouter.png" alt="Build Status"></a>                      |
| [DEA (dea_next)](https://github.com/cloudfoundry/dea_ng)                      | The droplet execution agent (DEA) performs two key activities in Cloud Foundry: staging and hosting applications.                                                   |<a href="https://travis-ci.org/cloudfoundry/dea_ng"><img src="https://travis-ci.org/cloudfoundry/dea_ng.png" alt="Build Status"></a>                          |
| [Health Manager](https://github.com/cloudfoundry/hm9000)                      | The health manager monitors the state of the applications and ensures that started applications are indeed running, their versions and number of instances correct. |<a href="https://travis-ci.org/cloudfoundry/health_manager"><img src="https://travis-ci.org/cloudfoundry/health_manager.png" alt="Build Status"></a>          |
| [UAA](https://github.com/cloudfoundry/uaa)                                    | The UAA (User Account and Authentication) is the identity management service for Cloud Foundry.                                           |<a href="https://travis-ci.org/cloudfoundry/uaa"><img src="https://travis-ci.org/cloudfoundry/uaa.png" alt="Build Status"></a>                          |
| [Login Server](https://github.com/cloudfoundry/login-server)                  | Handles authentication for Cloud Foundry and delegates all other identity management tasks to the UAA. Also provides OAuth2 endpoints issuing tokens to client apps for Cloud Foundry (the tokens come from the UAA and no data are stored locally).                                           |<a href="https://travis-ci.org/cloudfoundry/login-server"><img src="https://travis-ci.org/cloudfoundry/login-server.png" alt="Build Status"></a>                          |
| [Collector](https://github.com/cloudfoundry/collector)                                    | The collector will discover the various components on the message bus and query their /healthz and /varz interfaces.                                           |<a href="https://travis-ci.org/cloudfoundry/collector"><img src="https://travis-ci.org/cloudfoundry/collector.png" alt="Build Status"></a>                          |
| [Loggregator](https://github.com/cloudfoundry/loggregator)                                    | Loggregator is the user application logging subsystem for Cloud Foundry.                                           |<a href="https://travis-ci.org/cloudfoundry/loggregator"><img src="https://travis-ci.org/cloudfoundry/loggregator.png" alt="Build Status"></a>                          |



## Useful scripts

* `./update` pulls cf-release and updates all submodules (recursively) to the correct commit.
This is useful in the following situations:
  * After you've first cloned the repo
  * Before you make changes to the directory. (Running the script avoids having to rebase your changes on top of submodule updates.)
* `./commit_with_shortlog` commits changes you've made using `update_sub`.

## Ask Questions

Questions about the Cloud Foundry Open Source Project can be directed to our Google Groups.

* Cloud Foundry (aka VCAP) Developers: [https://groups.google.com/a/cloudfoundry.org/group/vcap-dev/topics](https://groups.google.com/a/cloudfoundry.org/group/vcap-dev/topics)
* BOSH Users:[https://groups.google.com/a/cloudfoundry.org/group/bosh-users/topics](https://groups.google.com/a/cloudfoundry.org/group/bosh-users/topics)
* BOSH Developers: [https://groups.google.com/a/cloudfoundry.org/group/bosh-dev/topics](https://groups.google.com/a/cloudfoundry.org/group/bosh-dev/topics)

## File a bug

Bugs can be filed using GitHub Issues in the respective repository of each [Cloud Foundry](http://github.com/cloudfoundry) component.

## Contributions

Please read the [contributors' guide](https://github.com/cloudfoundry/cf-release/blob/master/CONTRIBUTING.md) 

