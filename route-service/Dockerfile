FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} route-service.jar
ENTRYPOINT ["java","-jar","/route-service.jar"]