#!/bin/bash

curl -s localhost:4040/api/tunnels | grep -o -P '(?<="public_url":").*(?=","proto":"https")'
