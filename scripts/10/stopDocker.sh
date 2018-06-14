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
TMP_PREFIX=$tp

echo
echo "stopDocker.sh->Name is: $NAME"
echo "stopDocker.sh->Prefix is: $TMP_PREFIX"
echo



#$EAP_HOME/bin/$NAME.sh -c $PROFILE-for-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME


file="/tmp/${TMP_PREFIX}_running_${NAME}_docker.txt"
#if process is running, exit
if [ -f "$file" ]
then

	docker_id=`cat $file`
    #parent_process=$value


    echo "Stopping docker container $docker_id."
    docker stop $docker_id
    rm $file
    echo STOPPED
else
	echo "$file not found. already stopped"
	exit 1
fi

