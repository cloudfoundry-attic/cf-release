#!/bin/bash
touch /store/log/mongodb.log
mkdir -p /store/instance/data
exec /usr/bin/mongod --config /etc/mongodb.conf
