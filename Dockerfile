FROM openjdk:8

COPY target/universal/scapig-api-definition-*.tgz .
COPY start-docker.sh .
RUN chmod +x start-docker.sh
RUN tar xvf scapig-api-definition-*.tgz
EXPOSE 9010

CMD ["sh", "start-docker.sh"]