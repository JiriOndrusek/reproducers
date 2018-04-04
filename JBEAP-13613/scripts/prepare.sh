#!/bin/bash


echo "adding user"
$EAP_HOME/bin/add-user.sh -a -u 'quickstartUser' -p 'quickstartPwd1!' -g 'guest'
$EAP_HOME/bin/add-user.sh -u 'user' -p 'W3lcome!'

../../scripts/prepareConfiguration.sh --c node01  --d_p 8787 --p standalone-full-ha
../../scripts/prepareConfiguration.sh --c node02  --d_p 8888 --p standalone-full-ha
../../scripts/prepareConfiguration.sh --c node03  --d_p 8989 --p standalone-full-ha

./runAll.sh

p=$PWD


cd ../server

mvn clean install -Prh

cd $p


#node01
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --file=conf/node01.cli

cp $p/../server/target/JBEAP-13613-server-1.0.0-SNAPSHOT.jar $EAP_HOME-node01/deployments


#node01
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10090 --file=conf/node02.cli

cp $p/../server/target/JBEAP-13613-server-1.0.0-SNAPSHOT.jar $EAP_HOME-node02/deployments


#node01
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10190 --file=conf/node03.cli

cp $p/../server/target/JBEAP-13613-server-1.0.0-SNAPSHOT.jar $EAP_HOME-node03/deployments