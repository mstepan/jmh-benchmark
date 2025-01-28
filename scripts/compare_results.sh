#!/usr/bin/env bash
set -euxo pipefail

cp ../jmh-result-1.json jmh-result-1.json
cp ../jmh-result-2.json jmh-result-2.json

# Serve local assets
echo "To view results go to the following link:"
echo -e "\nhttp://jmh.morethan.io/?sources=http://localhost:3000/jmh-result-1.json,http://localhost:3000/jmh-result-2.json\n"
echo "Hit CTRL-C to stop the server"
(
  # Install http-server with:
  # npm install http-server -g
  http-server -s -p 3000 --cors
)