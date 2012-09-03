#!/bin/bash

# check whether the chown of /store has finished
for n in `seq 1 100`; do
  if [ -f /tmp/vcap_chown.out ]; then
    break
  fi
  sleep 0.1
done

mkdir -p /store/instance/mysql_tmp
mkdir -p /var/vcap/sys/run

/var/vcap/packages/mysql/libexec/mysql_warden.server start /store/instance/my.cnf
