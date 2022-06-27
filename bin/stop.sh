#!/bin/bash

basename=$(dirname "$0")
in_service=$("$basename"/status.sh | grep -v 'pid=')
if [[ "$in_service" == IN-SERVICE* ]]; then
   pid=$(ps -ewwf | grep comrade | grep -v grep | grep java | awk '{print $2}')
  kill "$pid"
   sleep 5
   in_service=$("$basename"/status.sh | grep -v 'pid=')
   if [[ "$in_service" == IN-SERVICE* ]]; then
      kill -9 "$pid"
   fi

   echo "SHUTDOWN"
else
   echo "ALREADY SHUTDOWN"
fi
