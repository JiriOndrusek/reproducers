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
    -s=*|--suspend=*)
    DEBUG_SUSPEND="${i#*=}"
    ;;
    -b=*|--background=*)
    background_input="${i#*=}"
    ;;
    *)
        NAME="${i#}"       # unknown option
    ;;
esac
done

if [ "$background_input" == "false" ]
then
    BACKGROUND='false'
else
    BACKGROUND='true'
fi

echo
echo "run.sh->Suspend is: $DEBUG_SUSPEND"
echo "run.sh->Name is: $NAME"
echo "run.sh->Background is: $BACKGROUND"
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

        declare -i debugPort=`jq ".servers[$i].configuration.debugPort" data.json`

        declare -i offset=`jq ".servers[$i].configuration.offset" data.json`

        echo
        echo "<><><><> starting $name <><><><><>"
        if [ 'n' == '$DEBUG_SUSPEND'  ]
        then
            echo "../../scripts/04/runConfiguration.sh -n=$name -t=$tmpPrefix -p=$profile -d=$debugPort -o=$offset -s='n' -b=$BACKGROUND"
            ../../scripts/07/runConfiguration.sh -n=$name -t=$tmpPrefix -p=$profile -d=$debugPort -o=$offset -s='n' -b=$BACKGROUND
        else
            echo "../../scripts/04/runConfiguration.sh -n=$name -t=$tmpPrefix -p=$profile -d=$debugPort -o=$offset -s=$suspend -b=$BACKGROUND"
            ../../scripts/07/runConfiguration.sh -n=$name -t=$tmpPrefix -p=$profile -d=$debugPort -o=$offset -s=$suspend -b=$BACKGROUND
        fi

    fi
done