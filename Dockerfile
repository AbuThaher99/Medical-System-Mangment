# Use Maven to build the project first
FROM maven:3.8.6-openjdk-22 AS build
WORKDIR /app

# Copy the project files
COPY . .

# Run Maven clean and install to build the JAR
RUN mvn clean install -DskipTests

# Use OpenJDK as the final runtime image
FROM openjdk:22
WORKDIR /app

# Copy the built JAR file from the Maven build stage
COPY --from=build /app/target/*.jar app.jar

# Start the application
ENTRYPOINT ["java", "-Dserver.port=$PORT", "-jar", "/app/app.jar"]
