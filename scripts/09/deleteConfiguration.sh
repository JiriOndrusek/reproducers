#!/bin/bash

while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        v="${1/--/}"
        declare $v="$2"
   fi

  shift
done

NAME=$c
echo "Deleted configuration name: $NAME"

SAME_DATA=$s_d
echo "Use same data folder: $SAME_DATA"


if [ "$SAME_DATA" == "y" ]
then
    rm -rf $EAP_HOME-nodes
else
    rm -rf $EAP_HOME-nodes-$NAME
fi

rm -f $EAP_HOME/bin/standalone-for-$NAME.sh
rm -f $EAP_HOME/bin/standalone-for-$NAME.conf