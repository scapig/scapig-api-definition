#!/bin/sh
sbt universal:package-zip-tarball
docker build -t scapig-api-definition .
docker tag scapig-api-definition scapig/scapig-api-definition
docker push scapig/scapig-api-definition
