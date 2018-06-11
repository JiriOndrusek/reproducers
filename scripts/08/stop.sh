#!/bin/bash
echo "---------------------------------------------"


declare -i count=`jq '.servers | length' data.json`

tmpPrefix=`jq '.tmpPrefix' data.json`
temp="${tmpPrefix%\"}"
tmpPrefix="${temp#\"}"

NAME=$1

    for (( i=0; i<$count; i++ ))
    do
        name=`jq ".servers[$i].name" data.json`
        temp="${name%\"}"
        name="${temp#\"}"

         version=`jq ".servers[$i].configuration.version" data.json`
        temp="${version%\"}"
        version="${temp#\"}"

    #    $EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --file=conf/$name.cli

        declare -i offset=`jq ".servers[$i].configuration.offset" data.json`

        if [ "$version" == "6" ]
        then
            declare -i remotePort=9999+offset
        else
            declare -i remotePort=9990+offset
        fi


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
            ../../scripts/03/stopConfiguration.sh --c $name --tp $tmpPrefix --rp $remotePort
        fi
    done

echo

