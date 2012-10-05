#!/bin/bash
for n in `seq 1 100`; do
  if [ -f /tmp/vcap_chown.out ]; then
    break
  fi
  sleep 0.1
done

touch /store/log/mongodb.log
mkdir -p /store/instance/data

# mongod_startup.sh 1.8
# mongod_startup.sh 1.8 --journal
# mongod_startup.sh 2.0
# mongod_startup.sh 2.0 --nojournal
version=$1
shift
args=$*
exec /usr/share/mongodb/mongodb-$version/mongod $args --config /etc/mongodb.conf
