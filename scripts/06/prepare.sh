#!/bin/bash

declare -i count=`jq '.servers | length' data.json`

declare -i usersCount=`jq ".users | length" data.json`


echo "<><><><><> adding user <><><><><>"
for (( u=0; u<$usersCount; u++ ))
do
    declare -i usersParam=`jq ".users[$u] | length" data.json`

    cmd=`jq ".users[$u][0]" data.json`
    temp="${cmd%\"}"
    cmd="${temp#\"}"

    temp=`jq ".users[$u][1]" data.json`
    temp="${temp%\"}"
    cmd="${cmd} -u ${temp#\"}"


    temp=`jq ".users[$u][2]" data.json`
    temp="${temp%\"}"
    cmd="${cmd} -p ${temp#\"}"

    temp=`jq ".users[$u][3]" data.json`
    temp="${temp%\"}"
    cmd="${cmd} -g ${temp#\"}"

    $EAP_HOME/bin/$cmd
done


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

    ../../scripts/06/prepareConfiguration.sh --c $name  --d_p $debugPort --p $profile

    javaOpts=${javaOpts:1:-1}

    echo JAVA_OPTS=\"$javaOpts\" >> $EAP_HOME/bin/$name.conf
done


./run.sh -s=n

#sleep 10s

p=$PWD

declare -i buildsCount=`jq ".builds | length" data.json`


echo "<><><><><> building <><><><><>"
for (( b=0; b<$buildsCount; b++ ))
do

    buildPath=`jq ".builds[$b]" data.json`
    temp="${buildPath%\"}"
    buildPath="${temp#\"}"

    ((b=b+1))


    buildCmd=`jq ".builds[$b]" data.json`
    temp="${buildCmd%\"}"
    buildCmd="${temp#\"}"

    cd $buildPath

    $buildCmd

done

cd $p

sleep 15s


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
    $EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:$remotePort --file=conf/$name.cli  --timeout=30000

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
        $EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:$remotePort --command="deploy --force --runtime-name=$rename $p/$deploy"
    done


done
