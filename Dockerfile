FROM openjdk:16-jdk-alpine3.13
MAINTAINER cleverconnect
COPY target/xmltojson-0.0.1.jar xmltojson-spring-0.0.1.jar
ENTRYPOINT ["java","-jar","/xmltojson-spring-0.0.1.jar"]