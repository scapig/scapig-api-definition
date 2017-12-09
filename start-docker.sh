#!/bin/sh
SCRIPT=$(find . -type f -name scapig-api-definition)
rm -f scapig-api-definition*/RUNNING_PID
exec $SCRIPT -Dhttp.port=7000
