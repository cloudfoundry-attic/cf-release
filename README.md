# Welcome to Cloud Foundry

Cloud Foundry is an open platform as a service (PaaS) that provides a choice of clouds, developer frameworks, and application services. Cloud Foundry makes it faster and easier to build, test, deploy, and scale applications.

This repository contains the Cloud Foundry source code.

Our documentation (currently a work in progress) is available here: [http://docs.cloudfoundry.org/](http://docs.cloudfoundry.org/).

## About Branches

The [**develop**](https://github.com/cloudfoundry/cf-release/tree/develop) branch is where we do active development. Although we endeavor to keep the [**develop**](https://github.com/cloudfoundry/cf-release/tree/develop) branch stable, we do not guarantee that any given commit will deploy cleanly.

If you want a stable branch, we recommend that you use the [**release-candidate**](https://github.com/cloudfoundry/cf-release/tree/release-candidate) branch.

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

## Useful scripts

* `./update` pulls cf-release and updates all submodules (recursively) to the correct commit.
This is useful in the following situations:
  * After you've first cloned the repo
  * Before you make changes to the directory. (Running the script avoids having to rebase your changes on top of submodule updates.)
* `./update_sub` takes as an argument the name of a submodule (partial matches okay) and pulls that submodule to master. The script then stages the changing of that submodule in cf-release.
Typically, only people developing Cloud Foundry should use `update_sub`.
This script is useful in the following situations:
  * After you've made changes to a submodule if you need those changes made available for deployment
* `./commit_with_shortlog` commits changes you've made using `update_sub`.

## Ask Questions

Questions about the Cloud Foundry Open Source Project can be directed to our Google Groups.

* BOSH Developers: [https://groups.google.com/a/cloudfoundry.org/group/bosh-dev/topics](https://groups.google.com/a/cloudfoundry.org/group/bosh-dev/topics)
* BOSH Users:[https://groups.google.com/a/cloudfoundry.org/group/bosh-users/topics](https://groups.google.com/a/cloudfoundry.org/group/bosh-users/topics)
* VCAP (Cloud Foundry) Developers: [https://groups.google.com/a/cloudfoundry.org/group/vcap-dev/topics](https://groups.google.com/a/cloudfoundry.org/group/vcap-dev/topics)

## File a bug

Bugs can be filed using GitHub Issues in the respective repository of each [Cloud Foundry](http://github.com/cloudfoundry) component.

## Contributions

Please read the [contributors' guide](https://github.com/cloudfoundry/cf-release/blob/master/CONTRIBUTING.md)
