# Welcome to Cloud Foundry

Cloud Foundry is an open platform as a service (PaaS), providing a choice of clouds, developer frameworks and application services. Cloud Foundry makes it faster and easier to build, test, deploy and scale applications.

This repository contains the Cloud Foundry source code.

Our documentation, currently a work in progress, is available here: [http://cloudfoundry.github.com/](http://cloudfoundry.github.com/)

## About Branches

The **master** branch is where we do active development. Although we endeavor to keep the **master** branch stable, we do not guarantee that any given commit will deploy cleanly.

If you want a stable branch, we recommend that you use the **release-candidate** branch.

## Repository Contents

This repository is structures for use with BOSH, an open source tool for release engineering, deployment and lifecycle management of large scale distributed services. The directories are for two purposes:

Source:

- **jobs**: start and stop commands for each of the jobs (processes) running on Cloud Foundry nodes.
- **packages**: packaging instructions used by BOSH to build each of the dependencies.
- **src**: the source code for the components in Cloud Foundry. Note that each of the components is a submodule with a pointer to a specific sha. So even if you do not use BOSH to deploy Cloud Foundry, the list of submodule pointers

Releases:

- **releases**: yml files containing the references to blobs for each package in a given release; these are solved within **.final_builds**
- **.final_builds**: references into the public blostore for final jobs & packages (each referenced by one or more **releases**)
- **config**: URLs and access credentials to the bosh blobstore for storing final releases
- **git**: Local git hooks

See the [documentation for deploying Cloud Foundry](http://cloudfoundry.github.com/docs/running/deploying-cf/) for more information about using BOSH.

In order to deploy Cloud Foundry with BOSH, you will need to create a manifest.
To do so, run `./generate_deployment_manifest <infrastructure-type>`, where `<infrastructure-type>` is one of `aws`, `vsphere`, or `warden`.
This script merges together several manifest stubs from the templates directory using the [spiff](https://github.com/cloudfoundry-incubator/spiff) tool, which must be installed beforehand.
Consult the [spiff repository](https://github.com/cloudfoundry-incubator/spiff) or [http://spiff.cfapps.io](http://spiff.cfapps.io) for more information on installing and using spiff.
A complete [sample manifest](http://cloudfoundry.github.com/docs/running/deploying-cf/vsphere/cloud-foundry-example-manifest.html) is also available in the Cloud Foundry documentation.

## Cloud Foundry Components (V2)

The current development effort centers on V2, also known as NG. For information on what the core team is working on, please see [our roadmap](http://cloudfoundry.github.com/docs/roadmap.html).

The components in a V2 deployment are:

<table>
	<tr>
		<td>Component</td><td>Description</td><td>Build Status</td>
	</tr>
	<tr>
		<td><b><a href="https://github.com/cloudfoundry/cloud_controller_ng">Cloud Controller (ccng)</a></b></td>
		<td>
			The primary entry point for Cloud Foundry. When you use vmc to push an application to Cloud Foundry, you target it against the Cloud Controller.
		</td>
		<td><a href="https://travis-ci.org/cloudfoundry/cloud_controller_ng"><img src="https://travis-ci.org/cloudfoundry/cloud_controller_ng.png" alt="Build Status"></a>
        </td>
	</tr>
	<tr>
		<td><b><a href="https://github.com/cloudfoundry/gorouter">gorouter</a></b></td>
		<td>The central router that manages traffic to applications deployed on Cloud Foundry. Written in go, the v2 router represents a significant performance improvement over v1.</td>
		<td><a href="https://travis-ci.org/cloudfoundry/gorouter"><img src="https://travis-ci.org/cloudfoundry/gorouter.png" alt="Build Status"></a>
		</td>
	</tr>
	<tr>
		<td><b><a href="https://github.com/cloudfoundry/dea_ng">DEA (dea_next)</a></b></td>
		<td>The droplet execution agent (DEA) performs two key activities in Cloud Foundry: staging and hosting applications.</td>
		<td><a href="https://travis-ci.org/cloudfoundry/dea_ng"><img src="https://travis-ci.org/cloudfoundry/dea_ng.png" alt="Build Status"></a></td>
	</tr>
	<tr>
		<td><b><a href="https://github.com/cloudfoundry/health_manager">Health Manager</a></b></td>
		<td>The health manager monitors the state of the applications and ensures that started applications are indeed running, their versions and number of instances correct.</td>
		<td><a href="https://travis-ci.org/cloudfoundry/health_manager"><img src="https://travis-ci.org/cloudfoundry/health_manager.png" alt="Build Status"></a>
        </td>
	</tr>
	<tr>
		<td></td>
		<td><i>documentation in progress...more to come</i></td>
		<td></td>
	</tr>
</table>

## Creating a release

Building a CF-Release later than 99be208f requires BOSH version 1.5.0 or later.
The latest version of BOSH CLI can be found here: https://github.com/cloudfoundry/bosh/tree/master/bosh_cli

## Useful scripts

* `./update` pulls cf-release and updates all submodules (recursively) to the correct commit.
This is useful after:
  * You've first cloned the repo
  * Before you make changes to the directory, to avoid a rebase
* `./update_sub` takes an argument of the name of a submodule (partial matches okay), pulls that submodule to master, and stages that change in cf-release.
Typically, only people developing Cloud Foundry should use `update_sub`.
This script is useful after:
  * You've made changes to a submodule and you need those changes made available for deployment
* `./commit_with_shortlog` commits changes you've made using `update_sub`.

## Ask Questions

Questions about the Cloud Foundry Open Source Project can be directed to our Google Groups.

* BOSH Developers: [https://groups.google.com/a/cloudfoundry.org/group/bosh-dev/topics](https://groups.google.com/a/cloudfoundry.org/group/bosh-dev/topics)
* BOSH Users:[https://groups.google.com/a/cloudfoundry.org/group/bosh-users/topics](https://groups.google.com/a/cloudfoundry.org/group/bosh-users/topics)
* VCAP (Cloud Foundry) Developers: [https://groups.google.com/a/cloudfoundry.org/group/vcap-dev/topics](https://groups.google.com/a/cloudfoundry.org/group/vcap-dev/topics)

## File a bug

Bugs can be filed using Github Issues within the various repositories of the [Cloud Foundry](http://github.com/cloudfoundry) components.

## Contributions

Please read the [contributors' guide](https://github.com/cloudfoundry/cf-release/blob/master/CONTRIBUTING.md)
