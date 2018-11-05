# DEPRECATED

v287 is the final version of cf-release

To deploy Cloud Foundry, use [cf-deployment](https://github.com/cloudfoundry/cf-deployment)

To transition a cf-release-based deployment to cf-deployment,
use the tools in [cf-deployment-transition](https://github.com/cloudfoundry/cf-deployment-transition)

The Release Integration team is no longer supporting this cf-release.
# Welcome to Cloud Foundry

Cloud Foundry is an open platform as a service (PaaS) that provides a choice of clouds, developer frameworks, and application services. Cloud Foundry makes it faster and easier to build, test, deploy, and scale applications.

This repository contains the Cloud Foundry source code. Cloud Foundry is deployed as a BOSH release.  See the [BOSH](http://bosh.io/) documentation for more information on BOSH.

**NB: Due to the symlinks present in cf-release, the v2 `bosh` CLI will fail to perform `create-release` against this repo.
You'll need to use the Ruby CLI for that, but you should be able to run `upload-release` and `deploy` using the new CLI.**

* [Documentation](http://docs.cloudfoundry.org/)
* [Release Notes](https://github.com/cloudfoundry/cf-release/releases)
* [Continuous Integration Pipeline](https://release-integration.ci.cf-app.com/teams/main/pipelines/cf-release)
* [Mailing List](https://lists.cloudfoundry.org/archives/list/cf-dev@lists.cloudfoundry.org/)

#### Table of Contents
1. [About Branches](#about-branches)
1. [Repository Contents](#repository-contents)
1. [Running Cloud Foundry](#running-cloud-foundry)
1. [Useful Scripts](#useful-scripts)
1. [Ask Questions](#ask-questions)
1. [File a Bug](#file-a-bug)
1. [Understanding Changes](#understanding-changes)
1. [Contributions](#contributions)

## About Branches

The [**develop**](https://github.com/cloudfoundry/cf-release/tree/develop) branch is where we do active development. Although we endeavor to keep the [**develop**](https://github.com/cloudfoundry/cf-release/tree/develop) branch stable, we do not guarantee that any given commit will deploy cleanly.

The [**release-candidate**](https://github.com/cloudfoundry/cf-release/tree/release-candidate) branch has passed all of our unit, integration, smoke, & acceptance tests, but has not been used in a final release yet. This branch should be fairly stable.

The [**master**](https://github.com/cloudfoundry/cf-release/tree/master) branch points to the most recent stable final release.

At semi-regular intervals a final release is created from the [**release-candidate**](https://github.com/cloudfoundry/cf-release/tree/release-candidate) branch. This final release is tagged and pushed to the [**master**](https://github.com/cloudfoundry/cf-release/tree/master) branch.

Pushing to any branch other than [**develop**](https://github.com/cloudfoundry/cf-release/tree/develop) will create problems for the CI pipeline, which relies on fast forward merges. To recover from this condition follow the instructions [here](docs/fix_commit_to_master.md).

## Repository Contents

This repository is structured for use with [BOSH](http://github.com/cloudfoundry/bosh); an open source tool for release engineering, deployment and lifecycle management of large scale distributed services. There are several directories of note:

- **jobs**: start and stop commands for each of the jobs (processes) running on Cloud Foundry nodes.
- **packages**: packaging instructions used by BOSH to build each of the dependencies.
- **src**: the source code for the components in Cloud Foundry. Note that each of the components is a submodule with a pointer to a specific SHA.
- **releases**: yml files containing the references to blobs for each package in a given release; these are solved within **.final_builds**
- **.final_builds**: references into the public blobstore for final jobs & packages (each referenced by one or more **releases**)
- **config**: URLs and access credentials to the bosh blobstore for storing final releases
- **git**: Local git hooks

## Running Cloud Foundry

Cloud Foundry can be run locally or in the cloud.  The best way to run Cloud Foundry is to deploy it using BOSH.  For more information about using BOSH, the [bosh-release repository](https://github.com/cloudfoundry/bosh) has links to documentation, mailing lists, and IRC channels.

To run BOSH and Cloud Foundry locally, use [BOSH-Lite](https://github.com/cloudfoundry/bosh-lite).  BOSH-Lite provisions a Vagrant VM running the BOSH director as well as [Garden-Linux](https://github.com/cloudfoundry-incubator/garden-linux) for creating Linux containers that simulate VMs in a real IaaS.

To run BOSH and Cloud Foundry in the cloud, there are several supported IaaS providers, primarily AWS, vSphere, and OpenStack.

Full instructions on infrastructure setup, building Cloud Foundry, and deploying Cloud Foundry with BOSH are available on our [documentation site](http://docs.cloudfoundry.org/deploying/).

## Useful Scripts

* `scripts/update` pulls cf-release and updates all submodules (recursively) to the correct commit.
This is useful in the following situations:
  * After you've first cloned the repo
  * Before you make changes to the directory. (Running the script avoids having to rebase your changes on top of submodule updates.)
* `scripts/setup-git-hooks` will ensure basic unit tests run before committing.
* `scripts/commit_with_shortlog` commits changes you've made to updated git submodules.

## Ask Questions

Questions about the Cloud Foundry Open Source Project can be directed to our Mailing Lists: 
[https://lists.cloudfoundry.org/mailman/listinfo](https://lists.cloudfoundry.org/mailman/listinfo)

There are lists for Cloud Foundry Developers, BOSH Users, and BOSH Developers.

## File a Bug

Bugs can be filed using GitHub Issues in the respective repository of each [Cloud Foundry](http://github.com/cloudfoundry) component.

## Understanding Changes

You can generate an HTML document which will show all commits between any two given SHAs, branches, tags, or other references, and then view it in your favourite browser:

```sh
$ bundle && bundle exec git_release_notes html --from=v210 --to=v212 > /tmp/changes.html && open /tmp/changes.html
```

## Contributions

Please read the [contributors' guide](https://github.com/cloudfoundry/cf-release/blob/master/CONTRIBUTING.md) 

