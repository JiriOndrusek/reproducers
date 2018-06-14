#!/bin/bash
echo
echo "********************************************************"
echo "****************** STOP.SH 10 **************************"
echo "********************************************************"
echo


declare -i count=`jq '.servers | length' data.json`

tmpPrefix=`jq '.tmpPrefix' data.json`
temp="${tmpPrefix%\"}"
tmpPrefix="${temp#\"}"

execution=`jq ".execution" data.json`
temp="${execution%\"}"
execution="${temp#\"}"

NAME=$1

    for (( i=0; i<$count; i++ ))
    do
        name=`jq ".servers[$i].name" data.json`
        temp="${name%\"}"
        name="${temp#\"}"

        declare -i offset=`jq ".servers[$i].configuration.offset" data.json`
        declare -i remotePort=9990+offset

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
            echo
            echo "<><><><> stopping $name <><><><><>"


            if [ "$execution" == "docker" ]
            then
                ../../scripts/10/stopDocker.sh --c $name --tp $tmpPrefix --rp
            else
                ../../scripts/03/stopConfiguration.sh --c $name --tp $tmpPrefix --rp $remotePort
            fi


        fi
    done

echo

