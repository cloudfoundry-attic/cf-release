#!/bin/bash
BASE_DIR=$1
LOG_DIR=$2
TMP_DIR=$3
chown vcap:vcap -R $BASE_DIR
chown vcap:vcap -R $LOG_DIR
chown vcap:vcap -R $TMP_DIR
