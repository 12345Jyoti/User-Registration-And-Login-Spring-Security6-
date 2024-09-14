# Use an official OpenJDK image for Java 21
FROM openjdk:21

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the target directory to the container
COPY target/Application-0.0.1-SNAPSHOT.jar /app/Application-0.0.1-SNAPSHOT.jar

# Expose the port on which the Spring Boot app runs
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "Application-0.0.1-SNAPSHOT.jar"]
