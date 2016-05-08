#!/bin/bash
set -x

#taskset -c 4-23 \
java -jar target/redislap-1.0-SNAPSHOT-jar-with-dependencies.jar -h 127.0.0.1 -p 6379 -t 100 -r 10000 -b 15 -c 100 -op get -opTimeout 5000 -enableCluster true
