# ---- Build stage: compile and package the jar ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Cache dependencies first (faster rebuilds when only source changes)
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- Run stage: small runtime image with just the jar ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/get-it-1.0.0.jar app.jar
# The platform injects $PORT; the app reads it via application.properties.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
