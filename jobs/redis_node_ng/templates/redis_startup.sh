for n in `seq 1 100`; do
  if [ -f /tmp/vcap_chown.out ]; then
    break
  fi
  sleep 0.1
done

exec start-stop-daemon --start --quiet --chuid vcap --exec  /usr/bin/redis-server /store/instance/redis.conf
