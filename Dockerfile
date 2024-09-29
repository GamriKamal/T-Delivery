FROM openjdk:17-oracle
LABEL authors="mrirmag"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} auth_service.jar
ENTRYPOINT ["java","-jar","/auth_service.jar"]