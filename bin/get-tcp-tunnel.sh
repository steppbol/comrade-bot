#!/bin/bash

curl -s localhost:4041/api/tunnels | grep -o -P '(?<="public_url":").*(?=","proto":"tcp")'
