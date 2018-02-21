#!/bin/bash
echo "---------------------------------------------"
../../scripts/stopConfiguration.sh --c lb --tp JBEAP-13375

../../scripts/stopConfiguration.sh --c node01 --tp JBEAP-13375

../../scripts/stopConfiguration.sh --c node02 --tp JBEAP-13375