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
# Memory-safe startup for small (e.g. 512 MB free-tier) instances:
#  * MaxRAMPercentage=45  -> ~230 MB heap, leaving room for metaspace, threads
#    and code cache so the kernel does NOT OOM-kill the JVM before Tomcat binds.
#  * MaxMetaspaceSize     -> bounds class metadata so total memory stays in budget.
#  * UseSerialGC          -> lowest memory/CPU overhead GC, ideal for 1 small core.
#  * ExitOnOutOfMemoryError-> if memory is ever exhausted, exit immediately and
#    visibly (fast restart) instead of hanging until the port-scan times out.
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=45.0", \
  "-XX:MaxMetaspaceSize=128m", \
  "-XX:+UseSerialGC", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-jar", "app.jar"]
