touch /store/log/vblob.log
exec /usr/bin/node /var/vcap/packages/vblob/server.js -f /store/instance/config.json
