# Use the official OpenJDK 21 image from the Docker Hub
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY connector/target /app

# Define the command to run the application
CMD ["java", "-jar", "frontend-0.0.1-SNAPSHOT.jar"]