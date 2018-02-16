#!/bin/bash
while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        v="${1/--/}"
        declare $v="$2"
   fi

  shift
done

NAME=$c
echo "New configuration name: $NAME"

TMP_PREFIX=$tp
echo "Used tmp prefix: $TMP_PREFIX"

#$EAP_HOME/bin/$NAME.sh -c $PROFILE-for-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME

#!/bin/bash

file="/tmp/${TMP_PREFIX}_running_${NAME}.txt"
#if process is running, exit
if [ -f "$file" ]
then
	echo "killing ..."
else
	echo "$file not found."
	echo "CAN NOT STOP."
	exit 1
fi

parent_process=`cat $file`
#parent_process=$value

pkill -9 -P $parent_process
kill -9 $parent_process

rm $file
echo STOPPED