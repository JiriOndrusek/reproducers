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
    -det=*|--detached=*)
    DETACHED="${i#*=}"
    ;;
    -ip=*|--op_address=*)
    IP="${i#*=}"
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
echo "runConfiguration.sh->Detached is: $DETACHED"
echo "runConfiguration.sh->IP Adress is: $IP"
echo


#$EAP_HOME/bin/$NAME.sh -c $PROFILE-$NAME.xml -b 127.0.0.1 -Djboss.server.base.dir=$EAP_HOME-$NAME
declare -i off=$OFFSET

file="/tmp/${TMP_PREFIX}_running_${NAME}_docker.txt"
    #if process is running, exit
    if [ -f "$file" ]
    then
        echo "$file found."
        echo "EXITTING"
        exit 1
    else
        echo ".... starting ....."
    fi

sed -i "s/${DEBUG_PORT},server=y,suspend=n/${DEBUG_PORT},server=y,suspend=${DEBUG_SUSPEND}/g" $EAP_HOME/bin/standalone-for-$NAME.conf
sed -i "s/${DEBUG_PORT},server=y,suspend=y/${DEBUG_PORT},server=y,suspend=${DEBUG_SUSPEND}/g" $EAP_HOME/bin/standalone-for-$NAME.conf

declare -i local8080=8080+$off
declare -i local8443=8443+$off
declare -i local9990=9990+$off

COMMAND="-p $local8080:8080 -p $local8443:8443 -p $DEBUG_PORT:8787 -p $local9990:9990 --net mynet123 --ip ${IP} eap_wrapper/01 ./bin/standalone-for-$NAME.sh -c $PROFILE-for-$NAME.xml -bprivate ${IP} -bmanagement 0.0.0.0 -Djboss.server.base.dir=/configuration -Djava.net.preferIPv4Stack=true -Djboss.node.name=$NAME"
echo $COMMAND

if [ "$DETACHED" == "y" ]
then
    echo ../../docker/run $EAP_HOME ${EAP_HOME}-nodes-${NAME} -d ${COMMAND}
    ../../docker/run $EAP_HOME ${EAP_HOME}-nodes-${NAME} -d $COMMAND > $file
else
    echo 'cmd' > $file
    echo ../../docker/run $EAP_HOME ${EAP_HOME}-nodes-${NAME} $COMMAND
    ../../docker/run $EAP_HOME ${EAP_HOME}-nodes-${NAME} $COMMAND
     rm $file
    echo STOPPED
fi

#///home/jondruse/git/projects/reproducers/scripts/09/runConfiguration.sh
