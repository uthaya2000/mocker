FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src/
RUN mvn clean install

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/mocker.jar /app/mocker.jar
EXPOSE 8445
CMD ["java", "-jar", "mocker.jar"]