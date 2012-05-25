ENABLE_REDIS="yes"
if [ -f /etc/default/redis ]; then . /etc/default/redis; fi
if [ "x$ENABLE_REDIS" = "xyes" ]; then exec start-stop-daemon --start --quiet --chuid vcap --exec  /usr/bin/redis-server /store/instance/redis.conf; fi
