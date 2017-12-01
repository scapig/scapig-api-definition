FROM openjdk:8

COPY target/universal/tapi-api-definition-*.tgz .
COPY start-docker.sh .
RUN chmod +x start-docker.sh
RUN tar xvf tapi-api-definition-*.tgz

EXPOSE 7000