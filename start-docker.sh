#!/bin/sh
SCRIPT=$(find . -type f -name tapi-api-definition)
exec $SCRIPT -Dhttp.port=7000
