#!/bin/bash

./stopAll.sh

../../scripts/deleteConfiguration.sh --c lb
../../scripts/deleteConfiguration.sh --c node01
../../scripts/deleteConfiguration.sh --c node02