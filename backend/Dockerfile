FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /a
COPY pom.xml .
COPY src src
RUN mvn -q package -DskipTests
FROM eclipse-temurin:21-jre
COPY --from=build /a/target/app.jar /app.jar
ENV JAVA_TOOL_OPTIONS="-Xmx350m"
ENTRYPOINT ["java","-jar","/app.jar"]
