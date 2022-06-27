#!/bin/bash

kill -9 "$(ps -ef | grep 'ngrok tcp' | grep -v 'grep' | awk '{print $2}')"
