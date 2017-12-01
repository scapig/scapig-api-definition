## tapi-api-definition

## Building
``
sbt clean test it:test component:test
``

## Packaging
``
sbt universal:package-zip-tarball
docker build -t tapi-api-definition .
``

## Running
``
docker run -p7000:7000 -i -a stdin -a stdout -a stderr tapi-api-definition sh start-docker.sh
``