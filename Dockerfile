# Use Java base image
FROM openjdk:17-jdk

# Set working directory
WORKDIR /app

# Copy jar file
COPY build/libs/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]