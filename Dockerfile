# Use a Maven image to build the application
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Use a minimal OpenJDK image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/kolkatacallgirl-0.0.1-SNAPSHOT.jar /app/kolkatacallgirl-0.0.1-SNAPSHOT.jar

# Expose the port the application will run on
EXPOSE 8085

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/kolkatacallgirl-0.0.1-SNAPSHOT.jar"]
