#!/bin/bash
for n in `seq 1 100`; do
  if [ -f /tmp/vcap_chown.out ]; then
    break
  fi
  sleep 0.1
done

touch /store/log/mongodb.log
mkdir -p /store/instance/data
exec /usr/bin/mongod --config /etc/mongodb.conf
