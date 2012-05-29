ENABLE_VBLOB="yes"
if [ -f /etc/default/vblob ]; then . /etc/default/vblob; fi
if [ "x$ENABLE_VBLOB" = "xyes" ]; then exec /usr/bin/node /var/vcap/packages/vblob/server.js -f /store/instance/config.json; fi
