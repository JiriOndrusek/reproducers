#!/bin/bash

echo copying certificates

cp -R ../data/certs $EAP_HOME/standalone/configuration

echo



../../scripts/03/prepare.sh