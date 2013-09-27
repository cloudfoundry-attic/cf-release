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
        
1. All contributions should be sent using GitHub "Pull Requests", which is the only way the project will accept them and creates a nice audit trail and structured approach.

The originating github user has to either have a github id on-file with the list of approved users that have signed the CLA or they can be a public "member" of a GitHub organization for a group that has signed the corporate CLA. This enables the corporations to manage their users themselves instead of having to tell us when someone joins/leaves an organization. By removing a user from an organization's GitHub account, their new contributions are no longer approved because they are no longer covered under a CLA.

If a contribution is deemed to be covered by an existing CLA, then it is analyzed for engineering quality and product fit before merging it.

If a contribution is not covered by the CLA, then the automated CLA system notifies the submitter politely that we cannot identify their CLA and ask them to sign either an individual or corporate CLA. This happens automatially as a comment on pull requests.

When the project receives a new CLA, it is recorded in the project records, the CLA is added to the database for the automated system uses, then we manually make the Pull Request as having a CLA on-file.

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
