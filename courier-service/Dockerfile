FROM openjdk:17-oracle
LABEL authors="mrirmag"
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} courier_service.jar
ENTRYPOINT ["java","-jar","/courier_service.jar"]