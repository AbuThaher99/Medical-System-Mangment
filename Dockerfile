FROM maven:3.8.6-openjdk-22 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:22
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-Dserver.port=$PORT", "-jar", "/app/app.jar"]
