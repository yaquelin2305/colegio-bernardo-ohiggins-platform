# ==========================================================================
# DOCKERFILE — API Gateway (:8080)
# Colegio Bernardo O'Higgins Platform
#
# Multi-stage build: compila en Maven + JDK, ejecuta solo con JRE.
# El Gateway es reactivo (WebFlux/Netty) — no usa Tomcat.
# ==========================================================================

# ─── STAGE 1: BUILD ──────────────────────────────────────────────────────
# Maven 3.9.6 + Eclipse Temurin JDK 17.
# --platform=$BUILDPLATFORM: compila nativo para la arch del builder.
FROM --platform=$BUILDPLATFORM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Capa cacheable: descarga dependencias Maven primero.
# Docker reusa esta capa si pom.xml no cambió.
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Compilación del JAR (sin tests — los tests requieren el MS-usuario
# corriendo y no están implementados aún).
COPY src ./src
RUN mvn clean package -DskipTests -B -q

# ─── STAGE 2: RUNTIME ────────────────────────────────────────────────────
# JRE 17 — no incluye JDK, Maven ni herramientas de compilación.
FROM eclipse-temurin:17-jre
WORKDIR /app

# Usuario no-root por seguridad.
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

COPY --from=builder /app/target/api-gateway-1.0.0.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

# Health check: el Gateway expone /actuator/health.
# 15s de intervalo porque el Gateway no tiene JPA ni schema sync,
# arranca más rápido que los MS de negocio.
HEALTHCHECK --interval=15s --timeout=5s --retries=10 --start-period=60s \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# JVM flags para contenedores (ver Dockerfile de ms-usuario para detalles).
# SPRING_PROFILES_ACTIVE default = "prod" (JWT + RBAC + rutas Docker).
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
