FROM openjdk:21

WORKDIR /app

COPY target/Application-0.0.1-SNAPSHOT.jar Application-0.0.1-SNAPSHOT.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "Application-0.0.1-SNAPSHOT.jar"]
