FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} menu-service.jar
ENTRYPOINT ["java","-jar","/menu-service.jar"]