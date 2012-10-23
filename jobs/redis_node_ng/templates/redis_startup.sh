for n in `seq 1 100`; do
  if [ -f /tmp/vcap_chown.out ]; then
    break
  fi
  sleep 0.1
done

# redis_startup.sh 2.2 - start redis 2.2.x
# redis_startup.sh 2.4 - start redis 2.4.x
version=$1
shift
exec start-stop-daemon --start --quiet --chuid vcap --exec  /usr/share/redis/redis-$version/redis-server /store/instance/redis.conf
