#!/bin/bash

while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        v="${1/--/}"
        declare $v="n"
   fi

  shift
done

echo

DEBUG_SUSPEND=$d_s
echo "Debug suspend is: $DEBUG_SUSPEND"


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

    if [ "$1" == "$name" ]
    then
        temp="true"
    elif [ -z "$1" ]
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
        if [ "n" == "$DEBUG_SUSPEND"  ]
        then
            ../../scripts/02/runConfiguration.sh --c $name --tp $tmpPrefix --p $profile --d_p $debugPort --o $offset --b true --d_s 'n'
        else
            ../../scripts/02/runConfiguration.sh --c $name --tp $tmpPrefix --p $profile --d_p $debugPort --o $offset --b true --d_s $suspend
        fi

    fi
done