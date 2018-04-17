#!/bin/bash


echo "adding user"
$EAP_HOME/bin//add-user.sh -a -g users -u admin -p admin123+

declare -i count=`jq '.servers | length' data.json`

for (( i=0; i<$count; i++ ))
do

    name=`jq ".servers[$i].name" data.json`
    temp="${name%\"}"
    name="${temp#\"}"

    profile=`jq ".servers[$i].configuration.profile" data.json`
    temp="${profile%\"}"
    profile="${temp#\"}"

    declare -i debugPort=`jq ".servers[$i].configuration.debugPort" data.json`

    javaOpts=`jq ".servers[$i].configuration.javaOpts" data.json`

    ../../scripts/prepareConfiguration.sh --c $name  --d_p $debugPort --p $profile

    javaOpts=${javaOpts:1:-1}

    echo $javaOpts >> $EAP_HOME/bin/$name.conf
done


./run.sh

sleep 10s

p=$PWD

cd ..

mvn clean install -Prh

cd $p

sleep 5s


for (( i=0; i<$count; i++ ))
do

    name=`jq ".servers[$i].name" data.json`
    temp="${name%\"}"
    name="${temp#\"}"

#    $EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --file=conf/$name.cli

    declare -i deployCount=`jq ".servers[$i].deploy | length" data.json`


    declare -i offset=`jq ".servers[$i].configuration.offset" data.json`
    declare -i remotePort=9990+offset

    echo
    echo "<><><><> configuring $name <><><><><>"
    $EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:$remotePort --file=conf/$name.cli

    for (( d=0; d<$deployCount; d++ ))
    do
        deploy=`jq ".servers[$i].deploy[$d]" data.json`
        temp="${deploy%\"}"
        deploy="${temp#\"}"
        ((d=d+1))
        rename=`jq ".servers[$i].deploy[$d]" data.json`
        temp="${rename%\"}"
        rename="${temp#\"}"



        echo
        echo "<><><><> deployintg $deploy on server $name <><><><><>"
        cp $p/$deploy $EAP_HOME-$name/deployments/$rename
    done


done
