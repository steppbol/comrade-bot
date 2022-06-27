#!/bin/bash

comrade_bin=$(realpath -s "$(dirname "$0")")
is_down=$("$comrade_bin"/status.sh | grep -v 'pid=')

if [[ "$is_down" == "STOPPED" ]]; then
  nohup java -jar comrade-1.0.0.jar --server.port=8988 >/dev/null 2>&1 &
  echo "STARTED"
else
  echo "ALREADY IN-SERVICE"
fi
