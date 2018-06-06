#!/bin/bash
while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        v="${1/--/}"
        declare $v="$2"
   fi

  shift
done

NAME=$c
echo "Configuration name: $NAME"

tail ${EAP_HOME}-nodes/log/server-for-$NAME.log -f