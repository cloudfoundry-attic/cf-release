#!/bin/bash
for n in `seq 1 100`; do
  if [ -f /tmp/vcap_chown.out ]; then
    break
  fi
  sleep 0.1
done

touch /store/log/mongodb.log
mkdir -p /store/instance/data

# mongod_startup.sh 18
# mongod_startup.sh 20
# mongod_startup.sh 20 --nojournal
if [ $# -gt 0 ]; then
  if [ $1 = "18" ]; then
    exec /usr/share/mongodb/mongodb18/mongod --config /etc/mongodb.conf
  elif [ $1 = "20" ]; then
    if [ $# -gt 1 ] && [ $2 = "--nojournal" ]; then
      exec /usr/share/mongodb/mongodb20/mongod --nojournal --config /etc/mongodb.conf
    else
      exec /usr/share/mongodb/mongodb20/mongod --config /etc/mongodb.conf
    fi
  fi
fi
