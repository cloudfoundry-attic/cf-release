exec start-stop-daemon --start --quiet --chuid vcap --exec  /usr/bin/redis-server /store/instance/redis.conf
