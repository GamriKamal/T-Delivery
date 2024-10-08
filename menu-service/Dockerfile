FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} menu-service.jar
ENTRYPOINT ["java","-jar","/menu-service.jar"]