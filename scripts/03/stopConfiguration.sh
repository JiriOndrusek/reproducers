#!/bin/bash
while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        v="${1/--/}"
        declare $v="$2"
   fi

  shift
done

echo

NAME=$c
echo "*** $NAME ***"

TMP_PREFIX=$tp
REMOTE_PORT=$rp

#$EAP_HOME/bin/$NAME.sh -c $PROFILE-for-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME

#!/bin/bash

file="/tmp/${TMP_PREFIX}_running_${NAME}.txt"
#if process is running, exit
if [ -f "$file" ]
then

	parent_process=`cat $file`
    #parent_process=$value

    if [ $parent_process == 'cmd' ]
    then
        echo "!!!!!!!!!!!!!! started from cmd !!!!!!!!!!!!!!"
    else
        echo "stopping ..."

        $EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:$REMOTE_PORT --command=":shutdown()"

        rm $file
        echo STOPPED
    fi
else
	echo "$file not found. already stopped"
	exit 1
fi

