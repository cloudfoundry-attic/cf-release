#!/usr/bin/env sh

if [ $# -ne 1 ]
then
    echo "Usage: setup_trafficlog_forwarder.sh [Config dir]"
    exit 1
fi

CONFIG_DIR=$1

#To enable rsyslog to read "nginx/router.access.log" under this folder
chmod o+x /var/vcap/data/sys/log/

cp $CONFIG_DIR/trafficlog_forwarder.conf /etc/rsyslog.d/10-trafficlog_forwarder.conf

/usr/sbin/service rsyslog restart
