#!/bin/bash
while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        v="${1/--/}"
        declare $v="$2"
   fi

  shift
done

echo -------------------------------
NAME=$c
echo "New configuration name: $NAME"

TMP_PREFIX=$tp
echo "Used tmp prefix: $TMP_PREFIX"

PROFILE=$p
echo "Used profile: $PROFILE"

OFFSET=$o
echo "Used offset: $OFFSET"

if [ $b == 'true' ]
then
    BACKGROUND='true'
else
    BACKGROUND='false'
fi

echo "Run in background: $BACKGROUND"
echo -------------------------------

#$EAP_HOME/bin/$NAME.sh -c $PROFILE-for-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME
declare -i off=$OFFSET

file="/tmp/${TMP_PREFIX}_running_${NAME}.txt"
    #if process is running, exit
    if [ -f "$file" ]
    then
        echo "$file found."
        echo "EXITTING"
        exit 1
    else
        echo "starting ....."
    fi


if [ $BACKGROUND == 'true' ]
then
    nohup $EAP_HOME/bin/$NAME.sh -c $PROFILE-for-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME -Djboss.socket.binding.port-offset=$off -Djboss.node.name=$NAME 2>&1 &
    echo $! > $file
else
    echo 'cmd' > $file
    $EAP_HOME/bin/$NAME.sh -c $PROFILE-for-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME -Djboss.socket.binding.port-offset=$off -Djboss.node.name=$NAME

    rm $file
    echo STOPPED
fi
