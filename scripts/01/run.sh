#!/bin/bash


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

        declare -i offset=`jq ".servers[$i].configuration.offset" data.json`

        echo
        echo "<><><><> starting $name <><><><><>"
        ../../scripts/runConfiguration.sh --c $name --tp $tmpPrefix --p $profile --o $offset --b true
    fi
done