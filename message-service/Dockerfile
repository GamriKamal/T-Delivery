FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} message-service.jar
ENTRYPOINT ["java","-jar","/message-service.jar"]