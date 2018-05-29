#!/usr/bin/env bash

./kill.sh

./prepare.sh

sleep 10s
#run client with 1000 messages
java -jar ../client/target/JBEAP-2531_lost-messages-after-XA-failover-client-1.0.0-SNAPSHOT.jar

#deploy app on live
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --command="deploy --force --runtime-name=live ../backup/target/JBEAP-2531_lost-messages-after-XA-failover-backup-1.0.0-SNAPSHOT.jar"

#deploy on backup node app to receive
sleep 10s

#deploy on backup
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10090 --command="deploy --force --runtime-name=live ../backup/target/JBEAP-2531_lost-messages-after-XA-failover-backup-1.0.0-SNAPSHOT.jar"

#copy journal to cli tool of artemis

#create dump to file