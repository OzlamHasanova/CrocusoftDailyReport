# Use a base image with Java installed
FROM openjdk:19

# Set the working directory inside the container
WORKDIR /app
EXPOSE 8080

# Copy the JAR file into the container
COPY target/*.jar CrocusoftDailyReport-0.0.1-SNAPSHOT.jar
#COPY CrocusoftDailyReport-0.0.1-SNAPSHOT.jar  CrocusoftDailyReportApplication.jar

# Expose the port that your application listens on

# Define the command to run your application
ENTRYPOINT ["java", "-jar", "CrocusoftDailyReport-0.0.1-SNAPSHOT.jar"]
