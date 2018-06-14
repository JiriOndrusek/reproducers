#!/bin/bash

echo
echo
echo "<><><><><><><><><><>"


for i in "$@"
do
case $i in
    -n=*|--name=*)
    NAME="${i#*=}"
    ;;
    -d=*|--detached=*)
    DETACHED="${i#*=}"
    ;;
    *)
        NAME="${i#}"       # unknown option
    ;;
esac
done

echo
echo "run.sh->Name is: $NAME"
echo "run.sh->Detached is: $DETACHED"
#echo "run.sh->Background_input is: $background_input"
echo


declare -i count=`jq '.servers | length' data.json`

tmpPrefix=`jq '.tmpPrefix' data.json`
temp="${tmpPrefix%\"}"
tmpPrefix="${temp#\"}"

for (( i=0; i<$count; i++ ))
do
    name=`jq ".servers[$i].name" data.json`
    temp="${name%\"}"
    name="${temp#\"}"

    temp="false"

    if [ "$NAME" == "$name" ]
    then
        temp="true"
    elif [ -z "$NAME" ]
    then
        temp="true"
    fi

    if [ "$temp" == "true" ]
    then
        profile=`jq ".servers[$i].configuration.profile" data.json`
        temp="${profile%\"}"
        profile="${temp#\"}"

        suspend=`jq ".servers[$i].configuration.debugSuspend" data.json`
        temp="${suspend%\"}"
        suspend="${temp#\"}"

        ip=`jq ".servers[$i].configuration.ip" data.json`
        temp="${ip%\"}"
        ip="${temp#\"}"

        declare -i debugPort=`jq ".servers[$i].configuration.debugPort" data.json`

        declare -i offset=`jq ".servers[$i].configuration.offset" data.json`

        echo
        echo "<><><><> starting $name <><><><><>"
        if [ 'n' == '$DEBUG_SUSPEND'  ]
        then
            echo "../../scripts/09/runConfigurationInDocker.sh -n=$name -t=$tmpPrefix -p=$profile -d=$debugPort -o=$offset -s='n' -det=$DETACHED" -ip=${ip}
            ../../scripts/10/runConfigurationInDocker.sh -n=$name -t=$tmpPrefix -p=$profile -d=$debugPort -o=$offset -s='n' -b=$BACKGROUND -det=$DETACHED -ip=${ip}
        else
            echo "../../scripts/09/runConfigurationInDocker.sh -n=$name -t=$tmpPrefix -p=$profile -d=$debugPort -o=$offset -s=$suspend --det=$DETACHED" -ip=${ip}
            ../../scripts/10/runConfigurationInDocker.sh -n=$name -t=$tmpPrefix -p=$profile -d=$debugPort -o=$offset -s=$suspend -det=$DETACHED -ip=${ip}
        fi

    fi
done