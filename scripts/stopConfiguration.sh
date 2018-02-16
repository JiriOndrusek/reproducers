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

PROFILE=$p
echo "Used profile: $PROFILE"

TMP_PREFIX=$tp
echo "Used tmp prefix: $TMP_PREFIX"

#$EAP_HOME/bin/$NAME.sh -c $PROFILE-for-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME

#!/bin/bash

file="/tmp/$TMP_PREFIX_save_pid.txt"
#if process is running, exit
if [ -f "$file" ]
then
	echo "$file found."
	echo "EXITTING"
	exit 1
else
	echo "starting ....."
fi

nohup $EAP_HOME/bin/$NAME.sh -c $PROFILE-for-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME 2>&1 &
echo $! > $file

tail $EAP_HOME/standalone/log/server.log -f