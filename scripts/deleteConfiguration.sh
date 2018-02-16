#!/bin/bash

NAME=$1
PROFILE=$2

echo "Starting '$NAME' for server '$EAP_HOME'"

$EAP_HOME/bin/$NAME.sh -c $PROFILE-for-$1.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME