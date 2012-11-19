#!/bin/bash
DATA_DIR=/store/instance
LOG_DIR=/store/log
POSTMASTER_PID_FILE=$DATA_DIR/postmaster.pid

master_pid=`head -n 1 $POSTMASTER_PID_FILE`
if [ ! -z "$master_pid" ]; then
  if [ -e /proc/$master_pid/oom_score_adj ]; then
    echo -1000 > /proc/$master_pid/oom_score_adj
  elif [ -e /proc/$master_pid/oom_adj ]; then
    # oom_adj is deprecated in 2.6.36+
    echo -17 > /proc/$master_pid/oom_adj
  fi
fi
