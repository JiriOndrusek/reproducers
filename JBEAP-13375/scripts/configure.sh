#!/bin/bash

../../scripts/prepareConfiguration.sh --c lb  --d_p 8787 --p standalone
../../scripts/prepareConfiguration.sh --c node01  --d_p 8888 --p standalone-ha
../../scripts/prepareConfiguration.sh --c node02  --d_p 8989 --p standalone-ha

./runAll.sh

p=$PWD


cd ../server-side

mvn clean install -Prh

cd $p

echo "lb"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --command="/socket-binding-group=standard-sockets/socket-binding=mod-cluster-adv:add(multicast-address=224.0.1.105,multicast-port=23666,interface=public)"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --command="/subsystem=undertow/configuration=filter/mod-cluster=modcluster:add(advertise-socket-binding=mod-cluster-adv,management-socket-binding=http)"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --command="/subsystem=undertow/server=default-server/host=default-host/filter-ref=modcluster:add()"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --command="/subsystem=logging/logger=org.jboss.modcluster:add()"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --command="/subsystem=logging/logger=org.jboss.modcluster:write-attribute(name=\"level\", value=\"DEBUG\")"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:9990 --command="reload"


echo "node1"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10090 --command="/socket-binding-group=standard-sockets/socket-binding=modcluster:write-attribute(name=multicast-port,value=23666)"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10090 --command="/subsystem=logging/logger=org.jboss.modcluster:add()"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10090 --command="/subsystem=logging/logger=org.jboss.modcluster:write-attribute(name=\"level\", value=\"DEBUG\")"
#$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10090 --command="/subsystem=undertow/server=default-server/host=default-host/setting=http-invoker:add(http-authentication-factory=application-http-authentication)"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10090 --command="reload"

cp $p/../server-side/target/JBEAP-13375-server-side-1.0.0-SNAPSHOT.jar $EAP_HOME-node01/deployments
cp /home/jondruse/git/projects/jboss-eap-quickstarts/ejb-remote/server-side/target/ejb-remote-server-side.jar $EAP_HOME-node01/deployments


echo "node2"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10190 --command="/socket-binding-group=standard-sockets/socket-binding=modcluster:write-attribute(name=multicast-port,value=23666)"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10190 --command="/subsystem=logging/logger=org.jboss.modcluster:add()"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10190 --command="/subsystem=logging/logger=org.jboss.modcluster:write-attribute(name=\"level\", value=\"DEBUG\")"
$EAP_HOME/bin/jboss-cli.sh -c --controller=127.0.0.1:10190 --command="reload"

cp $p/../server-side/target/JBEAP-13375-server-side-1.0.0-SNAPSHOT.jar $EAP_HOME-node02/deployments
cp /home/jondruse/git/projects/jboss-eap-quickstarts/ejb-remote/server-side/target/ejb-remote-server-side.jar $EAP_HOME-node02/deployments