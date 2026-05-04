# ===== STAGE 1: BUILD =====
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Green IT: cache de dependencias separado del código fuente
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

COPY src ./src
RUN mvn clean package -DskipTests -B -q

# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Seguridad: usuario no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/api-gateway-1.0.0.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

# Health check — el Gateway expone /actuator/health
HEALTHCHECK --interval=15s --timeout=5s --retries=10 --start-period=60s \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
