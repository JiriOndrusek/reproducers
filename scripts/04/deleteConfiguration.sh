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

rm -f $EAP_HOME/bin/$NAME.sh
rm -f $EAP_HOME/bin/$NAME.conf