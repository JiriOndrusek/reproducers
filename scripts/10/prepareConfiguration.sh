#!/bin/bash

echo "********************************************************"
echo "**********   PREPARE_CONFIGURATION.SH 10 ***************"
echo "********************************************************"

#--c configuration name
#--d_p debug port
#--d_s debug suspend
#--p profile
#--j_o java options to append

# https://unix.stackexchange.com/questions/129391/passing-named-arguments-to-shell-scripts
while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        v="${1/--/}"
        declare $v="$2"
   fi

  shift
done

NAME=$c
echo "New configuration name: $NAME"

DEBUG_PORT=$d_p
echo "Debug port is: $DEBUG_PORT"

PROFILE=$p
echo "Used profile: $PROFILE"

SAME_DATA=$s_d
echo "Use same data folder: $SAME_DATA"


if [ "$SAME_DATA" == "y" ]
then
    cp -R $EAP_HOME/standalone $EAP_HOME-nodes/

    cp -R $EAP_HOME/standalone/configuration/$PROFILE.xml $EAP_HOME-nodes/configuration/$PROFILE-for-$NAME.xml

    sed -i "s/\"server.log\"/\"server-for-$NAME.log\"/g" $EAP_HOME-nodes/configuration/$PROFILE-for-$NAME.xml

else
# different folders
    cp -R $EAP_HOME/standalone $EAP_HOME-nodes-$NAME/

    cp -R $EAP_HOME/standalone/configuration/$PROFILE.xml $EAP_HOME-nodes-$NAME/configuration/$PROFILE-for-$NAME.xml

    sed -i "s/\"server.log\"/\"server-for-$NAME.log\"/g" $EAP_HOME-nodes-$NAME/configuration/$PROFILE-for-$NAME.xml

fi


cp -R $EAP_HOME/bin/standalone.conf $EAP_HOME/bin/standalone-for-$NAME.conf

cp -R $EAP_HOME/bin/standalone.sh $EAP_HOME/bin/standalone-for-$NAME.sh

sed -i "2i RUN_CONF=${EAP_HOME}/bin/standalone-for-${NAME}.conf" $EAP_HOME/bin/standalone-for-$NAME.sh

sed -i '/8787/s/^#//g' $EAP_HOME/bin/standalone-for-$NAME.conf

sed -i "s/8787/${DEBUG_PORT}/g" $EAP_HOME/bin/standalone-for-$NAME.conf



