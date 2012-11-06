#!/bin/bash

# check whether the chown of /store has finished
for n in `seq 1 100`; do
  if [ -f /tmp/vcap_chown.out ]; then
    break
  fi
  sleep 0.1
done

mkdir -p /store/instance/mysql_tmp
if test ! -d /store/instance/data/mysql; then
  rsync -arl /var/vcap/packages/mysql/initdb/* /store/instance/data
fi

/var/vcap/packages/mysql/libexec/mysql_warden.server start /var/vcap/jobs/mysql_node_ng/config/my.cnf
