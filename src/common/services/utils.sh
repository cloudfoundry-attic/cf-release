source /var/vcap/packages/common/utils.sh

RUN_DIR=/var/vcap/sys/run/warden
LOG_DIR=/var/vcap/sys/log/warden
PIDFILE=$RUN_DIR/warden.pid
ROOT_DIR=/var/vcap/data/warden/rootfs
ROOT_TGZ=/var/vcap/stemcell_base.tar.gz
LOOP_DEVICE_COUNT=1024

setup_warden() {
    mkdir -p $RUN_DIR
    mkdir -p $LOG_DIR

    pid_guard $PIDFILE "Warden"
    echo $$ > $PIDFILE

    dpkg --install --skip-same-version $PKG_DIR/libnl1_1.1-5build1_amd64.deb
    dpkg --install --skip-same-version $PKG_DIR/quota_3.17-6_amd64.deb

    # Extract rootfs if needed
    if [ ! -d $ROOT_DIR ]
    then
        # Extract to temporary path, then rename to target path.
        # This makes sure that it is not possible that we end up with directory
        # that contains a partially extracted archive.
        mkdir -p $(dirname $ROOT_DIR)
	TMP=$(mktemp --tmpdir=$(dirname $ROOT_DIR) -d)
	chmod 755 $TMP
	tar -C $TMP -zxf $ROOT_TGZ
	mv $TMP $ROOT_DIR
    fi

    # Create loop devices for disk quota
    for i in $(seq 0 $(expr $LOOP_DEVICE_COUNT - 1)); do
      file=/dev/loop${i}
      if [ ! -b ${file} ]; then
        mknod -m0660 ${file} b 7 ${i}
        chown root.disk ${file}
      fi
    done
}

start_warden() {
  cd $PKG_DIR/warden

  export PATH=/var/vcap/packages/ruby/bin:$PATH

  exec /var/vcap/packages/ruby/bin/bundle exec \
       rake warden:start[$JOB_DIR/config/warden.yml] \
       >>$LOG_DIR/warden.stdout.log \
       2>>$LOG_DIR/warden.stderr.log
}
