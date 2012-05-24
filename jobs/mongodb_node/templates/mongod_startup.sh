#!/bin/bash
touch /store/log/mongodb.log
mkdir -p /store/instance/data
ENABLE_MONGODB="yes"
if [ -f /etc/default/mongodb ]; then . /etc/default/mongodb; fi
if [ "x$ENABLE_MONGODB" = "xyes" ]; then exec /usr/bin/mongod --config /etc/mongodb.conf; fi
