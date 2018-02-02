## scapig-api-definition

## Building
``
sbt clean test it:test component:test
``

## Packaging
``
sbt universal:package-zip-tarball
docker build -t scapig-api-definition .
``

## Publishing
``
docker tag scapig-api-definition scapig/scapig-api-definition
docker login
docker push scapig/scapig-api-definition
``

## Running
``
docker run -p9010:9010 -d scapig/scapig-api-definition
``
