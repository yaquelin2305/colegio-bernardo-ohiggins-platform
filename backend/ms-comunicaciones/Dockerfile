# ===== ETAPA 1: COMPILACIÓN (BUILDER) =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app


COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B


FROM eclipse-temurin:17-jre-alpine
WORKDIR /app


COPY --from=builder /app/target/*.jar app.jar


EXPOSE 8081

HEALTHCHECK --interval=10s --timeout=3s \
    CMD wget --spider -q http://localhost:8081/actuator/health || exit 1


ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]