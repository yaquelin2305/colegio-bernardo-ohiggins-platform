# ===== STAGE 1: BUILD =====
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Green IT: cache de dependencias separado del código fuente
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ===== STAGE 2: RUNTIME =====
# JRE Alpine: imagen mínima ~180MB (sin JDK, sin Maven, sin fuentes)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Seguridad: usuario no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/api-gateway-1.0.0.jar app.jar

# Puerto del Gateway
EXPOSE 8080

# JVM optimizada para contenedores
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
