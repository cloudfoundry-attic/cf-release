SCRIPT=$(basename $0)
mkdir -p /var/vcap/sys/log/monit

exec 1>> /var/vcap/sys/log/monit/$SCRIPT.log
exec 2>> /var/vcap/sys/log/monit/$SCRIPT.err.log

RUN_DIR=/var/vcap/sys/run/warden
LOG_DIR=/var/vcap/sys/log/warden
PIDFILE=$RUN_DIR/warden.pid

source /var/vcap/packages/common/utils.sh

prepare_warden() {
  job_name=$1
  src=$2
  dest=$3

  mkdir -p $RUN_DIR
  mkdir -p $LOG_DIR
  echo $$ > $PIDFILE

  export PATH=/var/vcap/packages/ruby/bin:$PATH

  # copy all the necessities to correct positions
  for (( i = 0; i < ${#src[@]}; i++ )) do
    mkdir -p "${dest[$i]}"
    [ -d ${src[$i]} ] && cp -af ${src[$i]}/* ${dest[$i]} || cp -af ${src[$i]} ${dest[$i]}
  done
}

start_warden() {
  job_name=$1
  service_startup=$2

  warden_conf_dir=/var/vcap/jobs/$job_name/config
  warden_root_dir=/var/vcap/packages/$job_name/warden/root/linux/base/rootfs

  cd /var/vcap/packages/$job_name/warden

  chmod 0755 $service_startup
  chmod 0644 $warden_root_dir/etc/init/services.conf

  exec /var/vcap/packages/ruby/bin/bundle exec \
       rake warden:start[$warden_conf_dir/warden.yml] \
       >>$LOG_DIR/warden.stdout.log \
       2>>$LOG_DIR/warden.stderr.log
}
