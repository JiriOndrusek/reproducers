#!/bin/bash

../../scripts/prepareConfiguration.sh --c lb  --d_p 8787 --p standalone
../../scripts/prepareConfiguration.sh --c node01  --d_p 8888 --p standalone-ha
../../scripts/prepareConfiguration.sh --c node02  --d_p 8989 --p standalone-ha

./runAll.sh