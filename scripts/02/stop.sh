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
        echo "<><><><> stopping $name <><><><><>"
        ../../scripts/02/stopConfiguration.sh --c $name --tp $tmpPrefix

    done
else
    echo
    echo "<><><><> stopping $1 <><><><><>"
    ../../scripts/02/stopConfiguration.sh --c $1 --tp $tmpPrefix
fi

echo

