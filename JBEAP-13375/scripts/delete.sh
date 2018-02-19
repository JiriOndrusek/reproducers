#!/bin/bash

while true; do
    read -p "Do you wish to delete all configurations?" yn
    case $yn in
        [Nn]* ) exit;;
        [Yy]* ) echo deleting ...; break;;
        * ) echo "Please answer y or n.";;
    esac
done

./stopAll.sh

../../scripts/deleteConfiguration.sh --c lb
../../scripts/deleteConfiguration.sh --c node01
../../scripts/deleteConfiguration.sh --c node02

