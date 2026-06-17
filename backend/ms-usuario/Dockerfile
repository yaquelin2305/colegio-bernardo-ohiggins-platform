# ==========================================================================
# DOCKERFILE — MS-Usuario (:8083)
# Colegio Bernardo O'Higgins Platform
#
# Multi-stage build: compila en Maven + JDK, ejecuta solo con JRE.
# Esto reduce la imagen final ~40% (sin JDK, sin Maven, sin src).
# ==========================================================================

# ─── STAGE 1: BUILD ──────────────────────────────────────────────────────
# Usa Maven + JDK 17 para compilar. --platform detecta arch del host
# (amd64/arm64) automáticamente.
FROM --platform=$BUILDPLATFORM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Capa 1: solo pom.xml → descarga dependencias (cacheable por Docker)
# Si pom.xml no cambia, esta capa se reusa en builds posteriores.
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Capa 2: código fuente → compila el JAR (sin tests para velocidad)
COPY src ./src
RUN mvn clean package -DskipTests -B -q

# ─── STAGE 2: RUNTIME ────────────────────────────────────────────────────
# Solo JRE 17, mucho más liviano (~200MB vs ~500MB con JDK completo).
FROM eclipse-temurin:17-jre
WORKDIR /app

# Seguridad: crea usuario no-root (appuser). Si alguien compromete el
# contenedor, no obtiene acceso root al host.
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

COPY --from=builder /app/target/ms-usuario-1.0.0.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8083

# Health check: consulta /actuator/health cada 10s.
# 12 reintentos × 10s = 120s máximo para arrancar (el JPA + schema sync
# puede tardar 60-90s en cold start).
HEALTHCHECK --interval=10s --timeout=5s --retries=12 --start-period=90s \
    CMD wget -qO- http://localhost:8083/actuator/health | grep -q '"status":"UP"' || exit 1

# JVM flags optimizados para contenedores:
# -XX:+UseContainerSupport → detecta límites de CPU/RAM del contenedor
# -XX:MaxRAMPercentage=75.0 → no usa más del 75% de RAM asignada
# -XX:+UseG1GC              → Garbage Collector eficiente para apps server
# -Djava.security.egd=...   → acelera el arranque (evita bloqueo por /dev/random)
# SPRING_PROFILES_ACTIVE    → por defecto "prod", sobreescribible en docker-compose
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
