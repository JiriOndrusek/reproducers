#!/bin/bash

../../scripts/runConfiguration.sh --c node01 --tp JBEAP-13613 --p standalone-full-ha --o 0 --b true

../../scripts/runConfiguration.sh --c node02 --tp JBEAP-13613 --p standalone-full-ha --o 100 --b true