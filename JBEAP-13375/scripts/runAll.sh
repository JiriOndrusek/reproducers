#!/bin/bash

../../scripts/runConfiguration.sh --c lb --tp JBEAP-13375 --p standalone --o 0

../../scripts/runConfiguration.sh --c node01 --tp JBEAP-13375 --p standalone-ha --o 100

../../scripts/runConfiguration.sh --c node02 --tp JBEAP-13375 --p standalone-ha --o 200