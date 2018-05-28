##!/bin/bash
#while [ $# -gt 0 ]; do
#
#   if [[ $1 == *"--"* ]]; then
#        v="${1/--/}"
#        declare $v="$2"
#   fi
#
#  shift
#done

for i in "$@"
do
case $i in
    -n=*|--name=*)
    NAME="${i#*=}"
    ;;
    -t=*|--tmp_prefix=*)
    TMP_PREFIX="${i#*=}"
    ;;
    -p=*|--profile=*)
    PROFILE="${i#*=}"
    ;;
    -o=*|--offset=*)
    OFFSET="${i#*=}"
    ;;
    -d=*|--debug_port=*)
    DEBUG_PORT="${i#*=}"
    ;;
    -s=*|--debug_suspend=*)
    DEBUG_SUSPEND="${i#*=}"
    ;;
    -b=*|--bacground=*)
    BACKGROUND="${i#*=}"
    ;;
    *)
        NAME="${i#}"       # unknown option
    ;;
esac
done
#echo "runConfiguration.sh->b is: $BACKGROUND"


echo
echo "runConfiguration.sh->Name is: $NAME"
echo "runConfiguration.sh->Tmp prefix is: $TMP_PREFIX"
echo "runConfiguration.sh->Profile: $PROFILE"
echo "runConfiguration.sh->Offset: $OFFSET"
echo "runConfiguration.sh->Debug port is: $DEBUG_PORT"
echo "runConfiguration.sh->Suspend is: $DEBUG_SUSPEND"
echo "runConfiguration.sh->Background is: $BACKGROUND"
echo


#$EAP_HOME/bin/$NAME.sh -c $PROFILE-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME
declare -i off=$OFFSET

file="/tmp/${TMP_PREFIX}_running_${NAME}.txt"
    #if process is running, exit
    if [ -f "$file" ]
    then
        echo "$file found."
        echo "EXITTING"
        exit 1
    else
        echo ".... starting ....."
    fi

sed -i "s/${DEBUG_PORT},server=y,suspend=n/${DEBUG_PORT},server=y,suspend=${DEBUG_SUSPEND}/g" $EAP_HOME/bin/$NAME.conf
sed -i "s/${DEBUG_PORT},server=y,suspend=y/${DEBUG_PORT},server=y,suspend=${DEBUG_SUSPEND}/g" $EAP_HOME/bin/$NAME.conf


if [ "$BACKGROUND" == "true" ]
then
    echo "nohup $EAP_HOME/bin/$NAME.sh -c $PROFILE-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME -Djboss.socket.binding.port-offset=$off -Djboss.node.name=$NAME 2>&1 &"
    nohup $EAP_HOME/bin/$NAME.sh -c $PROFILE-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME -Djboss.socket.binding.port-offset=$off -Djboss.node.name=$NAME 2>&1 &
    echo $! > $file
else
    echo 'cmd' > $file
    echo "$EAP_HOME/bin/$NAME.sh -c $PROFILE-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME -Djboss.socket.binding.port-offset=$off -Djboss.node.name=$NAME"
    $EAP_HOME/bin/$NAME.sh -c $PROFILE-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME -Djboss.socket.binding.port-offset=$off -Djboss.node.name=$NAME

    rm $file
    echo STOPPED
fi

