#!/usr/bin/env bash

./delete.sh

./prepare.sh

java -jar ../client/target/JBEAP-3848_deleting-page-client-1.0.0-SNAPSHOT.jar

./kill.sh

./run.sh

sleep 4s

java -cp ../client/target/JBEAP-3848_deleting-page-client-1.0.0-SNAPSHOT.jar jondruse.ClientReceiver

./kill.sh

rm -rf ${EAP_HOME}-nodes-server/data/activemq/paging

./run.sh

sleep 4s

java -jar ../client/target/JBEAP-3848_deleting-page-client-1.0.0-SNAPSHOT.jar

./kill.sh

./run.sh

sleep 4s

java -cp ../client/target/JBEAP-3848_deleting-page-client-1.0.0-SNAPSHOT.jar jondruse.ClientReceiver