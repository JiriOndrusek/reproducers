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

SAME_DATA=$s_d
echo "Use same data folder: $SAME_DATA"

if [ "$SAME_DATA" == "y" ]
then
    tail ${EAP_HOME}-nodes/log/server-for-$NAME.log -f
else
    tail ${EAP_HOME}-nodes-$NAME/log/server-for-$NAME.log -f
fi