#!/bin/bash

sameData=`jq ".sameData" data.json`
temp="${sameData%\"}"
sameData="${temp#\"}"

../../scripts/09/showConfiguration.sh  --s_d $sameData --c $1