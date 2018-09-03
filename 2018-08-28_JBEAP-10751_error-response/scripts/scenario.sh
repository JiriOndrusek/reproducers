#!/bin/bash

echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. starting cluster <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
./delete.sh
./prepare.sh

sleep 20s

echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. executing clients <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
$JMETER/jmeter -n -t ../jmeter/jmeter.jmx -l ../jmeter/tmp/results -e -o ../jmeter/tmp/res | tee ../jmeter/log

./kill.sh
