# Build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -Dmaven.repo.local=/root/.m2/repository -Dmaven.wagon.http.retryHandler.count=5 clean package -Dmaven.test.skip=true

# Usar una imagen base de OpenJDK para ejecutar
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=postgres"]
