#!/bin/bash

source init.sh




echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> starting cluster <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
echo "-------------- tmp = $1"

if [[ "$1" == "" ]]; then
    echo "TMP folder is not defined"
    exit
fi
./delete.sh
./prepare.sh

sleep 30s


echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> executing clients <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
#/home/jondruse/dev/apache-jmeter-4.0/bin/jmeter -n -t /home/jondruse/git/projects/reproducers/2018-08-28_JBEAP-10751_error-response/jmeter/jmeter.jmx -l $TMP_FOLDER/$1/results -e -o $TMP_FOLDER/$1/res | tee $TMP_FOLDER/$1/log

#gnome-terminal --geometry=120x30 --tab --title="EAP-perf18" -e "bash -c \"runClient.sh\""
gnome-terminal --geometry=120x30 --tab --title="EAP-perf18" -e "bash -c \"./show.sh p18\""
gnome-terminal --geometry=120x30 --tab --title="EAP-perf19" -e "bash -c \"./show.sh p19\""
gnome-terminal --geometry=120x30 --tab --title="client" -e "bash -c \"./client.sh $1\""

sleep 10s

echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> p19 gracefull shutdown <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
./stop.sh p19

sleep 20s
echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> p19 start again <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
./run.sh p19
#
sleep 120s
#echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> p18 gracefull shutdown <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
#./stop.sh p18
#
#sleep 5s
#echo ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> p18 start again <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"
#./run.sh p18
#
##wait for client to finish
#sleep 40s
#
./kill.sh

mv jmeter.log $TMP_FOLDER/$1/jmeter.log
./copyLogs.sh $1
pgrep -n -f "show.sh p18" | xargs kill -15
pgrep -n -f "show.sh p19" | xargs kill -15
