# Contributing to cf-release

The Cloud Foundry team uses GitHub and accepts contributions via
[pull request](https://help.github.com/articles/using-pull-requests)

The `cf-release` repository is a [BOSH](https://github.com/cloudfoundry/bosh)
release for Cloud Foundry.

If you wish to make a change to any of the components, submit a pull request to
those repositories directly. Once accepted those changes should make their way
into `cf-release`. All components are submodules in cf-release and can be found
in the [`src/`](https://github.com/cloudfoundry/cf-release/tree/master/src)
directory.

If you want to make changes to the `cf-release` itself, read on.

## Contributor License Agreement

Follow these steps to make a contribution to any of our open source repositories:

1. Ensure that you have completed our CLA Agreement for
  [individuals](http://www.cloudfoundry.org/individualcontribution.pdf) or
  [corporations](http://www.cloudfoundry.org/corpcontribution.pdf).

1. Set your name and email (these should match the information on your submitted CLA)

        git config --global user.name "Firstname Lastname"
        git config --global user.email "your_email@example.com"

## General Workflow

1. Fork the repository
1. Create a feature branch (`git checkout -b better_cf-release`)
1. Build and deploy the [checked out version of](http://docs.cloudfoundry.com/docs/running/deploying-cf/common/cf-release.html) `cf-release`
1. Make changes on your branch
1. Re-deploy your version of `cf-release`
1. [Run integration YETI tests](https://github.com/cloudfoundry/vcap-yeti)
1. Push to your fork (`git push origin better_cf-release`) and submit a pull request

We favor pull requests with very small, single commits with a single purpose.

Your pull request is much more likely to be accepted if:

* Your pull request includes tests

* Your pull request is small and focused with a clear message that conveys the intent of your change.
