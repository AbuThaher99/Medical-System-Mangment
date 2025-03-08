FROM openjdk:22
ARG JAR_FILE=target/ProjectTraining-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dserver.port=$PORT", "-jar", "/app.jar"]
