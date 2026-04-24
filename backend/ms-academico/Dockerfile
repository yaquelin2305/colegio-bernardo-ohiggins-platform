# ===== STAGE 1: BUILD =====
# Imagen completa de Maven para compilar — no va a producción
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Green IT: copiamos pom.xml primero para aprovechar cache de capas de Docker
# Si el pom no cambia, las dependencias no se re-descargan
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Ahora copiamos el código fuente
COPY src ./src

# Compilar y empaquetar, saltando tests (los tests se ejecutan en CI/CD por separado)
RUN mvn package -DskipTests -q

# ===== STAGE 2: RUNTIME =====
# Imagen mínima JRE — sin Maven, sin código fuente, sin herramientas innecesarias
# Green IT: imagen ~180MB vs ~500MB con JDK completo
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiar solo el JAR del stage de build
COPY --from=builder /app/target/ms-academico-1.0.0.jar app.jar

# Puerto que expone el microservicio
EXPOSE 8082

# JVM optimizada para contenedores (Green IT: límites de memoria explícitos)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
