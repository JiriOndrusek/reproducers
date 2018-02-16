#!/bin/bash

NAME=$1
DEBUG_PORT=$2
PROFILE=$3

echo "Creating new configuration with name '$NAME' for folder '$EAP_HOME'"

cp -R $EAP_HOME/standalone $EAP_HOME-$NAME/

cp -R $EAP_HOME/bin/standalone.conf $EAP_HOME/bin/$NAME.conf

cp -R $EAP_HOME/standalone/configuration/$PROFILE.xml $EAP_HOME-$NAME/standalone/$PROFILE-for-$NAME.xml

cp -R $EAP_HOME/bin/standalone.sh $EAP_HOME/bin/$NAME.sh

sed -i '2i RUN_CONF="$EAP_HOME/bin/$NAME.conf"' $EAP_HOME/bin/$NAME.sh

sed -i '/$DEBUG_PORT/s/^#//g' $EAP_HOME/bin/$NAME.conf