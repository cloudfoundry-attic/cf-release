# HM Workspace

A Go workspace for specifiying dependencies for [HM9000](http://github.com/cloudfoundry/hm9000).

## Usage
This repository doubles as a `$GOPATH`. It will automatically be set up for you if you have [direnv](http://direnv.net) installed.

         # fetch release repo
         $ cd $HOME
         $ git clone https://github.com/cloudfoundry/hm-workspace
         $ cd hm-workspace

         # automate $GOPATH and $PATH setup
         $ direnv allow

         # initialize and sync submodules
         $ git submodule update --init

If you do not wish to use direnv, you can simply `source` the `.envrc` file in the root of the release repo.  You may manually need to update your `$GOPATH` and `$PATH` variables as you switch in and out of the directory.
