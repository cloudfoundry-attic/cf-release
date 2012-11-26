#!/bin/bash
chown vcap:vcap -R /var/vcap/sys/run/mysqld
chown vcap:vcap -R /var/vcap/sys/log/mysql
chown vcap:vcap -R /var/vcap/data/mysql_tmp
