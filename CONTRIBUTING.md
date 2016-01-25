# Contributing to cf-release

The Cloud Foundry team uses GitHub and accepts contributions via
[pull request](https://help.github.com/articles/using-pull-requests).

The `cf-release` repository is a [BOSH](https://github.com/cloudfoundry/bosh)
release for Cloud Foundry.

If you wish to make a change to any of the [components](https://github.com/cloudfoundry/cf-release#cloud-foundry-components-v2),
submit a pull request to those repositories directly. Once accepted those changes
should make their way into `cf-release`. All components are submodules in cf-release
and can be found in the [`src/`](https://github.com/cloudfoundry/cf-release/tree/master/src)
directory.

To learn about how you can contribute to Cloud Foundry please visit our [contributing page](https://www.cloudfoundry.org/contribute/)

## Proposing new Features
Please see the [Proposing New Features](https://github.com/cloudfoundry-community/cf-docs-contrib/wiki/Proposing-New-Features) page on the Cloud Foundry community wiki that explains the process for getting the team's buy-in on your contribution before you start work.

## General Workflow

1. [Collaborate with the team](https://github.com/cloudfoundry-community/cf-docs-contrib/wiki/Proposing-New-Features) before you start work
1. [Fork](https://help.github.com/articles/fork-a-repo) the repository and make a local [clone](https://help.github.com/articles/fork-a-repo#step-2-create-a-local-clone-of-your-fork)
1. Create a feature branch from the development branch

   ```bash
   cd cf-release
   git checkout develop
   ./scripts/update
   git checkout -b better_cf-release

   ```
1. Build and deploy the checked out version of
   [`cf-release`](http://docs.cloudfoundry.org/bosh/create-release.html#dev-release). We recommend using 
   [bosh-lite](https://github.com/cloudfoundry/bosh-lite) for this.

   ```bash
   cd cf-release
   ./scripts/generate-bosh-lite-dev-manifest
   
   bosh create release
   bosh upload release
   bosh deploy
   ```
1. Make changes on your branch.
1. Update and run [manifest generation tests and other basic tests](spec) `./scripts/test`.
1. [Re-deploy](http://docs.cloudfoundry.org/deploying/) your version of `cf-release`.
1. [Run Cloud Foundry Acceptance Tests (CATS)](https://github.com/cloudfoundry/cf-acceptance-tests).  We recommend
   running the tests as a bosh errand (`bosh run errand acceptance_tests`). You won't see any output from the tests 
   until the errand completes.  If you choose to run them manually, please follow the instructions in the
   [CATS README](https://github.com/cloudfoundry/cf-acceptance-tests/blob/master/README.md).
1. Set up git hooks by running `./scripts/setup-git-hooks` and commit your work.  This will ensure that the same set of tests
   as `./scripts/test` run as a pre-commit hook before any commit.
1. Push to your fork (`git push origin better_cf-release`) and
   [submit a pull request](https://help.github.com/articles/creating-a-pull-request)
   selecting `develop` as the target branch.

We favor pull requests with very small, single commits with a single purpose.

Your pull request is much more likely to be accepted if:

* Your pull request includes tests. The runtime development team uses test driven development to help ensure high
  quality code and excellent test coverage.

* Your pull request is small and focused with a clear message that conveys the intent of your change.
