# check whether the chown of /store has finished
for n in `seq 1 100`; do
  if [ -f /tmp/vcap_chown.out ]; then
    break
  fi
  sleep 0.1
done

# start the instance
touch /store/log/vblob.log
exec /usr/bin/node /var/vcap/packages/vblob/server.js -f /store/instance/config.json
