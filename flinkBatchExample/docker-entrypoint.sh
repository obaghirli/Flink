#!/bin/sh

FLINK_HOME=${FLINK_HOME:-"/opt/flink/bin"}
/opt/flink-1.2.1/bin/start-local.sh
exec "$@"
