#!/bin/bash -l

set -e

[[ -s "$HOME/.rvm/scripts/rvm" ]] && source "$HOME/.rvm/scripts/rvm"

gem install bosh_cli --no-ri --no-rdoc
gem update bosh_cli

set +e
cd $WORKSPACE/cf-release
set -e

echo -e "---\ndev_name: appcloud" > config/dev.yml

bosh --non-interactive create release --force

bosh --non-interactive target $DIRECTOR
bosh --non-interactive login $BOSH_USER $BOSH_PASSWORD

UPLOAD_OUTPUT=$WORKSPACE/upload_output

bosh --non-interactive upload release --rebase | tee $UPLOAD_OUTPUT

# If no rebase was necessary, stop the build.
grep --quiet 'Error 100: Rebase is attempted without any job or package changes' $UPLOAD_OUTPUT && exit 0

RELEASE_NUM=`grep 'appcloud/[0-9]\+\.[0-9]\+' $UPLOAD_OUTPUT | sed -E 's/.*appcloud\/(.*) \(.*/\1/'`
SHA=`git rev-parse --verify HEAD`

echo "RELEASE_NUM = $RELEASE_NUM"
echo "SHA = $SHA"

set +e
cd $WORKSPACE/deployments
set -e

bosh --non-interactive diff p01/te-composite-template-staging.erb

$WORKSPACE/cf-release/src/common/ver_sub.rb $WORKSPACE/deployments/$DEPLOYMENT $RELEASE_NUM $SHA

bosh --non-interactive deployment $WORKSPACE/deployments/$DEPLOYMENT

bosh --non-interactive deploy

git commit $DEPLOYMENT -m "Automated staging deploy $BUILD_URL"
git push origin deploy:master
