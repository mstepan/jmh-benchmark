#!/usr/bin/env bash
set -euxo pipefail

echo "Comparing JMH results $1 and $2"

echo "To view results go to the following link:"
echo -e "\nhttp://jmh.morethan.io/?sources=http://localhost:3000/$1,http://localhost:3000/$2\n"
echo "Hit CTRL-C to stop the server"
(
  http-server -s -p 3000 --cors
)