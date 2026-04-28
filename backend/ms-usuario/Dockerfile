# =============================================================
# Dockerfile Multi-Stage — MS-Usuario
# Stage 1: Build con Maven
# Stage 2: Runtime mínimo con JRE 17 Alpine
# =============================================================

# ── STAGE 1: BUILD ────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copiar pom.xml primero para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar fuentes y compilar (skip tests en el build de imagen)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── STAGE 2: RUNTIME ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Usuario no-root por seguridad (principio de mínimo privilegio)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copiar el JAR desde el stage de build
COPY --from=builder /app/target/ms-usuario-1.0.0.jar app.jar

# Cambiar propietario al usuario no-root
RUN chown appuser:appgroup app.jar

USER appuser

# Puerto expuesto
EXPOSE 8083

# Health check para Docker y Compose
HEALTHCHECK --interval=10s --timeout=5s --retries=12 --start-period=90s \
    CMD wget -qO- http://localhost:8083/actuator/health | grep -q UP || exit 1

# Opciones JVM optimizadas para contenedor
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
