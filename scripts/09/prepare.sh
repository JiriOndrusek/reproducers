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
    cmd="${cmd} ${temp#\"}"


    temp=`jq ".users[$u][2]" data.json`
    temp="${temp%\"}"
    cmd="${cmd} -p ${temp#\"}"

    temp=`jq ".users[$u][3]" data.json`
    temp="${temp%\"}"
    cmd="${cmd} -g ${temp#\"}"

    echo "adding user by command:${cmd}"
    $EAP_HOME/bin/$cmd
done

echo "<><><><><> preparing <><><><><>"

for (( i=0; i<$count; i++ ))
do

    name=`jq ".servers[$i].name" data.json`
    temp="${name%\"}"
    name="${temp#\"}"

    sameData=`jq ".sameData" data.json`
    temp="${sameData%\"}"
    sameData="${temp#\"}"

    profile=`jq ".servers[$i].configuration.profile" data.json`
    temp="${profile%\"}"
    profile="${temp#\"}"

    declare -i debugPort=`jq ".servers[$i].configuration.debugPort" data.json`

    javaOpts=`jq ".servers[$i].configuration.javaOpts" data.json`

    echo "../../scripts/09/prepareConfiguration.sh --c $name  --d_p $debugPort --p $profile --s_d $sameData"
    ../../scripts/09/prepareConfiguration.sh --c $name  --d_p $debugPort --p $profile --s_d $sameData

    javaOpts=${javaOpts:1:-1}

    if [ "$javaOpts" != "" ]
    then
        echo JAVA_OPTS=\"$javaOpts\" >> $EAP_HOME/bin/standalone-for-$name.conf
    fi


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

echo "<><><><><> sleeping <><><><><>"

sleep=`jq ".sleep" data.json`
temp="${sleep%\"}"
sleep="${temp#\"}"


sleep ${sleep}s


for (( i=0; i<$count; i++ ))
do

    name=`jq ".servers[$i].name" data.json`
    temp="${name%\"}"
    name="${temp#\"}"

    version=`jq ".servers[$i].configuration.version" data.json`
    temp="${version%\"}"
    version="${temp#\"}"

#    $EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --file=conf/$name.cli

    declare -i deployCount=`jq ".servers[$i].deploy | length" data.json`


    declare -i offset=`jq ".servers[$i].configuration.offset" data.json`

    if [ "$version" == "6" ]
    then
        declare -i remotePort=9999+offset
    else
        declare -i remotePort=9990+offset
    fi

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