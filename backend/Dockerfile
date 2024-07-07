# Build stage
FROM --platform=linux/amd64 gradle:8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build -Pprofile=unit
#RUN gradle clean build -Pprofile=integration

# Package stage
FROM openjdk:21-ea-17-slim-buster
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/skmb-0.0.1.jar /app/app.jar
#EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]