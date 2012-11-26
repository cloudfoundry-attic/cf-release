#!/bin/bash
BASE_DIR=$1
LOG_DIR=$2
chown vcap:vcap -R $BASE_DIR
chown vcap:vcap -R $LOG_DIR
