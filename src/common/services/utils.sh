source /var/vcap/packages/common/utils.sh

WARDEN_RUN_DIR=/var/vcap/sys/run/warden
WARDEN_LOG_DIR=/var/vcap/sys/log/warden
WARDEN_PIDFILE=$WARDEN_RUN_DIR/warden.pid
WARDEN_ROOT_DIR=/var/vcap/data/warden/rootfs
WARDEN_ROOT_TGZ=/var/vcap/stemcell_base.tar.gz
LOOP_DEVICE_COUNT=1024

setup_warden() {
  use_loop_device=$1

  mkdir -p $WARDEN_RUN_DIR
  mkdir -p $WARDEN_LOG_DIR

  # Extract rootfs if needed
  if [ ! -d $WARDEN_ROOT_DIR ]
  then
    # Extract to temporary path, then rename to target path.
    # This makes sure that it is not possible that we end up with directory
    # that contains a partially extracted archive.
    mkdir -p $(dirname $WARDEN_ROOT_DIR)
    TMP=$(mktemp --tmpdir=$(dirname $WARDEN_ROOT_DIR) -d)
    chmod 755 $TMP
    tar -C $TMP -zxf $WARDEN_ROOT_TGZ
    mv $TMP $WARDEN_ROOT_DIR
  fi

  # Create loop devices for disk quota
  if [ ! -z ${use_loop_device} ] && [ ${use_loop_device} = 'true' ]
  then
    for i in $(seq 0 $(expr $LOOP_DEVICE_COUNT - 1)); do
      file=/dev/loop${i}
      if [ ! -b ${file} ]; then
        mknod -m0660 ${file} b 7 ${i}
        chown root.disk ${file}
      fi
    done
  fi
}

start_warden() {
  cd $PKG_DIR/warden

  export PATH=/var/vcap/packages/ruby/bin:$PATH

  pid_guard $WARDEN_PIDFILE "Warden"
  echo $$ > $WARDEN_PIDFILE

  exec /var/vcap/packages/ruby/bin/bundle exec \
       rake warden:start[$JOB_DIR/config/warden.yml] \
       >>$WARDEN_LOG_DIR/warden.stdout.log \
       2>>$WARDEN_LOG_DIR/warden.stderr.log
}

wait_warden_start() {
  while true
  do
    netstat -an | grep LISTENING | grep /tmp/warden\.sock > /dev/null
    if [ $? -eq 0 ]
    then
      break
    fi
    sleep 0.1
  done
}
