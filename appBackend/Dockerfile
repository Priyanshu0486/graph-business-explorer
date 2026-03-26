# Use an official Maven image with JDK 21 as a build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download project dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the project source code
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy the SAP data files for ingestion
COPY sap-o2c-data ./sap-o2c-data

# Set the data path environment variable
ENV APP_DATA_PATH=/app/sap-o2c-data

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
