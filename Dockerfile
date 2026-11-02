# syntax=docker/dockerfile:1

# Build stage: compile and package the jar with Maven (Java 21 JDK).
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
# Copy only the build files first, so dependencies are downloaded once and cached
# until pom.xml changes. The cache mount keeps ~/.m2 between builds.
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -B dependency:go-offline
COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw -q -B package -DskipTests

# Runtime stage: only the JRE and the jar, running as a non-root user.
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 app
COPY --from=build /app/target/auth-service-0.0.1-SNAPSHOT.jar app.jar
USER 1001
EXPOSE 8081
# Use up to 75% of the container's memory limit for the Java heap.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
