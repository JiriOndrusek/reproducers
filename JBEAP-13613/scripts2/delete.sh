#!/bin/bash

while true; do
    read -p "Do you wish to delete all configurations?" yn
    case $yn in
        [Nn]* ) exit;;
        [Yy]* ) echo deleting ...; break;;
        * ) echo "Please answer y or n.";;
    esac
done

./stop.sh


declare -i count=`jq '.servers | length' data.json`

tmpPrefix=`jq '.tmpPrefix' data.json`
temp="${tmpPrefix%\"}"
tmpPrefix="${tmpPrefix#\"}"

for (( i=0; i<$count; i++ ))
do
    name=`jq ".servers[$i].name" data.json`
    temp="${name%\"}"
    name="${temp#\"}"

    echo
    echo "<><><><> deleting $name <><><><><>"
    ../../scripts/deleteConfiguration.sh --c $name
done

echo



