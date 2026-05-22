# 📨 Microservicio de Comunicaciones (`ms-comunicaciones`)

Este microservicio se encarga de gestionar y despachar las comunicaciones del ecosistema (Notificaciones, Avisos, etc.) a través de múltiples canales empleando el patrón **Strategy** y bajo una **Arquitectura Hexagonal** (Puertos y Adaptadores).

Actualmente soporta el envío de mensajes mediante tres canales:
* **EMAIL** (Estrategia base implementada)
* **SMS** (Estrategia base implementada)
* **WHATSAPP** (Generación de enlaces profesionales *Click to Chat*)

---

## 🏗️ Arquitectura del Proyecto

El código está estructurado siguiendo los principios de la arquitectura limpia:
* **Domain (`domain.model`, `domain.port`)**: Contiene las reglas de negocio puras (Comunicaciones, Canales) y las interfaces de entrada/salida (Puertos).
* **Application (`application.service`)**: Implementa los casos de uso (`ComunicacionUseCase`) y coordina las estrategias de despacho.
* **Infrastructure (`infrastructure.adapter`)**: Contiene los adaptadores tecnológicos (Controladores REST para la entrada, JPA/PostgreSQL para la persistencia) y configuraciones de seguridad.

---

## 🚀 Requisitos Previos

* **Java 17** (Eclipse Temurin recomendado)
* **Maven 3.9+** (o puedes usar el wrapper `./mvnw` incluido)
* **PostgreSQL** (Base de datos)

---

## 🛠️ Variables de Entorno y Configuración

El servicio utiliza por defecto el puerto **`8081`**. Puedes configurar la conexión a la base de datos modificando las siguientes variables de entorno (o configurándolas en tu entorno de despliegue/Docker):

| Variable | Descripción | Valor por Defecto |
| :--- | :--- | :--- |
| `DB_URL` | URL de conexión JDBC a Postgres | `jdbc:postgresql://localhost:5432/colegio_db` |
| `DB_USERNAME` | Usuario de la base de datos | `colegio` |
| `DB_PASSWORD` | Contraseña de la base de datos | `colegio123` |
| `SPRING_JPA_HIBERNATE_DDL-AUTO` | Estrategia de creación de tablas | `update` |

> ⚠️ **Nota:** El microservicio trabaja de forma aislada dentro del esquema `comunicaciones_schema` de PostgreSQL. El perfil de producción (`prod`) desactiva el cliente de Eureka.

---

## 🏃 Cómo Ejecutar el Microservicio

### En Desarrollo (Local)
1. Asegúrate de tener tu instancia de PostgreSQL corriendo con la base de datos `colegio_db`.
2. Compila y ejecuta la aplicación con Maven:
   ```bash
   ./mvnw spring-boot:run