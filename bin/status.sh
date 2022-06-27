#!/bin/bash

pid=$(ps -ewwf | grep comrade | grep -v grep | grep java | awk '{print $2}')
if [ -z "$pid" ]; then
  echo "STOPPED"
  exit 2
else
  echo "IN-SERVICE"
  echo "$pid"
  exit 0
fi
