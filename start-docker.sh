#!/bin/sh
SCRIPT=$(find . -type f -name tapi-api-definition)
exec $SCRIPT $HMRC_CONFIG -Dhttp.port=7000
