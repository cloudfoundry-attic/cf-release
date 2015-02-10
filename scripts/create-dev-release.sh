#!/bin/bash

set -e -x

CF_RELEASE_OUT="../create-release.out"

bosh -n --parallel 10 sync blobs
./scripts/unused_blobs
bosh -n create release --with-tarball | tee $CF_RELEASE_OUT

EXIT_STATUS=${PIPESTATUS[0]}
if [ ! "$EXIT_STATUS" = "0" ]; then
  echo "Failed to Create CF Release"
  exit $EXIT_STATUS
fi

VERSION=`grep -a "Release version" $CF_RELEASE_OUT | cut -d " " -f3`
if [ "$VERSION" = "" ]; then
  echo "No Release Version Found"
  exit 1
fi

MANIFEST_YML=`grep -a "Release manifest" $CF_RELEASE_OUT  | cut -d " " -f3`
if [ "$MANIFEST_YML" = "" ]; then
  echo "No Release Manifest Found"
  exit 1
fi

TARBALL=`grep -a "Release tarball" $CF_RELEASE_OUT | cut -d " " -f4`
if [ "$TARBALL" = "" ]; then
  echo "No Release Tarball Found"
  exit 1
fi

mkdir -p output

mv $MANIFEST_YML ./output/dev-manifest.yml
mv $TARBALL ./output/dev-release.tgz
mv $CF_RELEASE_OUT ./output/

exit 0
