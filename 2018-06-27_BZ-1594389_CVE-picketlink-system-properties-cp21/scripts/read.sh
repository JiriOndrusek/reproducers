#!/bin/bash

echo
echo
echo "-----------------------------------------------------"
cat $EAP_HOME-nodes-01/log/server-for-01.log | grep "ClusterConnectionBridge.*is connected"
echo "-----------------------------------------------------"
echo
