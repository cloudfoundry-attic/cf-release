#!/bin/bash

set -x -u -e

[ $# -eq 1 ] || ( echo "wrong number of arguments, expected 1 but got $#" ; exit 2 )
what=$1
ruby pkg_utils/transform_git_source $what $what/vendor/checkout
rm -rf $what/vendor/checkout
ruby pkg_utils/fetch_gems $what/Gemfile $what/Gemfile.lock $what/vendor/cache
