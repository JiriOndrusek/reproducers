#!/bin/bash
echo "---------------------------------------------"


declare -i count=`jq '.servers | length' data.json`

tmpPrefix=`jq '.tmpPrefix' data.json`
temp="${tmpPrefix%\"}"
tmpPrefix="${temp#\"}"


if [ -z "$1" ]
then
    for (( i=0; i<$count; i++ ))
    do
        name=`jq ".servers[$i].name" data.json`
        temp="${name%\"}"
        name="${temp#\"}"

        echo
        echo "<><><><> killing $name <><><><><>"
        ../../scripts/03/killConfiguration.sh --c $name --tp $tmpPrefix

    done
else
    echo
    echo "<><><><> killing $1 <><><><><>"
    ../../scripts/03/killConfiguration.sh --c $1 --tp $tmpPrefix
fi

echo

