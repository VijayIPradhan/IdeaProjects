# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container
COPY ./*.jar app1.jar

# Command to run the application
CMD ["java", "-jar", "app1.jar"]
