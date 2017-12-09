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

## Running
``
docker run -p7000:7000 -i -a stdin -a stdout -a stderr scapig-api-definition sh start-docker.sh
``