# 📚 MS-Académico — Microservicio de Gestión Académica

> Colegio Bernardo O'Higgins | Proyecto Fullstack III — Duoc UC

## Índice

- [Arquitectura Hexagonal](#arquitectura-hexagonal)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Diagrama de Flujo](#diagrama-de-flujo)
- [Patrones de Diseño](#patrones-de-diseño)
- [Endpoints API](#endpoints-api)
- [Variables de Entorno](#variables-de-entorno)
- [Ejecución Local](#ejecución-local)
- [Pruebas Unitarias y Cobertura](#pruebas-unitarias-y-cobertura)
- [Docker](#docker)

---

## Arquitectura Hexagonal

Este microservicio implementa **Arquitectura Hexagonal (Ports & Adapters)** estricta:

```
┌──────────────────────────────────────────────────────────────────┐
│                        INFRAESTRUCTURA                           │
│  ┌─────────────────┐              ┌──────────────────────────┐   │
│  │   REST API       │              │  PostgreSQL / JPA        │   │
│  │  (Adaptador IN)  │              │  (Adaptador OUT)         │   │
│  └────────┬────────┘              └───────────┬──────────────┘   │
│           │                                   │                  │
│    ┌──────▼───────────────────────────────────▼──────┐          │
│    │                  APLICACIÓN                      │          │
│    │  Puertos IN          │        Puertos OUT         │         │
│    │  (interfaces)        │        (interfaces)        │         │
│    │  StudentUseCase      │     StudentRepositoryPort  │         │
│    │  GradeUseCase        │     GradeRepositoryPort    │         │
│    │  AttendanceUseCase   │     AttendanceRepositoryPort│        │
│    │  ReportUseCase       │                            │         │
│    │         │            │                            │         │
│    │  ┌──────▼──────────────────────────────────┐     │         │
│    │  │            DOMINIO                       │     │         │
│    │  │  Student, Grade, Attendance              │     │         │
│    │  │  AcademicReport (Value Object)           │     │         │
│    │  │  Lógica de negocio pura                  │     │         │
│    │  └──────────────────────────────────────────┘     │         │
│    └───────────────────────────────────────────────────┘         │
└──────────────────────────────────────────────────────────────────┘
```

### Capas y responsabilidades

| Capa | Paquete | Contenido |
|------|---------|-----------|
| **Domain** | `domain.model` | Entidades puras sin frameworks |
| **Domain** | `domain.exception` | Excepciones de negocio |
| **Application** | `application.port.in` | Puertos de entrada (contratos) |
| **Application** | `application.port.out` | Puertos de salida (contratos) |
| **Application** | `application.service` | Casos de uso implementados |
| **Infrastructure** | `infrastructure.adapter.in.rest` | Controladores REST |
| **Infrastructure** | `infrastructure.adapter.out.persistence` | Adaptadores JPA |
| **Infrastructure** | `infrastructure.config` | Configuraciones Spring |
| **Infrastructure** | `infrastructure.factory` | Factory Method para reportes |

---

## Estructura del Proyecto

```
ms-academico/
├── src/
│   ├── main/
│   │   ├── java/cl/duoc/colegio/academico/
│   │   │   ├── MsAcademicoApplication.java
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Student.java
│   │   │   │   │   ├── Grade.java
│   │   │   │   │   ├── Attendance.java
│   │   │   │   │   └── AcademicReport.java
│   │   │   │   └── exception/
│   │   │   │       ├── AcademicoException.java
│   │   │   │       ├── StudentNotFoundException.java
│   │   │   │       ├── GradeNotFoundException.java
│   │   │   │       └── AttendanceNotFoundException.java
│   │   │   ├── application/
│   │   │   │   ├── port/
│   │   │   │   │   ├── in/  (StudentUseCase, GradeUseCase, AttendanceUseCase, ReportUseCase)
│   │   │   │   │   └── out/ (StudentRepositoryPort, GradeRepositoryPort, AttendanceRepositoryPort)
│   │   │   │   └── service/
│   │   │   │       ├── StudentService.java
│   │   │   │       ├── GradeService.java
│   │   │   │       ├── AttendanceService.java
│   │   │   │       └── ReportService.java
│   │   │   └── infrastructure/
│   │   │       ├── adapter/
│   │   │       │   ├── in/rest/        (StudentController, GradeController, etc.)
│   │   │       │   └── out/persistence/ (Entities, JpaRepositories, Adapters, Mappers)
│   │   │       ├── config/             (SecurityConfig, OpenApiConfig, GlobalExceptionHandler)
│   │   │       └── factory/            (AcademicReportFactory)
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/cl/duoc/colegio/academico/
│           ├── domain/model/           (StudentTest, GradeTest)
│           └── application/service/   (GradeServiceTest, AttendanceServiceTest, ReportServiceTest)
├── Dockerfile
├── pom.xml
└── README.md
```

---

## Diagrama de Flujo

Flujo completo de una petición desde el cliente hasta la base de datos:

```
Cliente / Frontend
        │
        ▼
  ┌──────────────┐
  │  API Gateway  │  ← Valida JWT, enruta a ms-academico
  │  (port 8080)  │
  └──────┬───────┘
         │  HTTP / Service Discovery (Eureka)
         ▼
  ┌──────────────┐
  │     BFF       │  ← Orquesta múltiples MSs (opcional)
  │  (port 8090)  │
  └──────┬───────┘
         │  HTTP → ms-academico
         ▼
  ┌──────────────────────────────────────────┐
  │            MS-ACADÉMICO (port 8082)       │
  │                                           │
  │  1. REST Controller recibe la petición    │
  │  2. Llama al UseCase (puerto de entrada)  │
  │  3. Service ejecuta la lógica             │
  │  4. Llama al RepositoryPort (puerto out)  │
  │  5. Persistence Adapter consulta JPA      │
  │  6. JPA → PostgreSQL (Supabase)           │
  └──────────────────────────────────────────┘
         │
         ▼
  ┌──────────────┐
  │  PostgreSQL   │  (Supabase cloud)
  └──────────────┘
```

---

## Patrones de Diseño

### 1. Repository Pattern
Separa el acceso a datos de la lógica de negocio mediante puertos de salida:
```
GradeUseCase → GradeRepositoryPort (interfaz) ← GradePersistenceAdapter (implementación JPA)
```

### 2. Factory Method — `AcademicReportFactory`
Centraliza la creación de `AcademicReport` con su tipo de alerta:
```java
AcademicReport report = AcademicReportFactory.crear(student, grades, porcentajeAsistencia);
// → Determina automáticamente: SIN_ALERTA / ALERTA_RENDIMIENTO / ALERTA_ASISTENCIA / ALERTA_CRITICA
```

### 3. Singleton via Spring Beans
Todos los `@Service` y `@Component` son Singletons gestionados por el contenedor IoC de Spring.
Un Bean = una única instancia compartida en toda la aplicación.

---

## Endpoints API

Swagger UI disponible en: `http://localhost:8082/swagger-ui.html`

### Estudiantes `/api/v1/estudiantes`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/` | Crear estudiante |
| `GET` | `/{id}` | Obtener por ID |
| `GET` | `/rut/{rut}` | Obtener por RUT |
| `GET` | `/` | Listar todos |
| `GET` | `/curso/{curso}` | Listar por curso |
| `PUT` | `/{id}` | Actualizar |
| `DELETE` | `/{id}` | Eliminar |

### Notas `/api/v1/notas`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/` | Registrar nota |
| `GET` | `/{id}` | Obtener por ID |
| `GET` | `/estudiante/{studentId}` | Notas del estudiante |
| `GET` | `/estudiante/{studentId}/asignatura/{asignatura}` | Notas por asignatura |
| `GET` | `/estudiante/{studentId}/promedio` | Calcular promedio general |
| `GET` | `/estudiante/{studentId}/promedio/asignatura/{asignatura}` | Promedio por asignatura |
| `PUT` | `/{id}` | Actualizar nota |
| `DELETE` | `/{id}` | Eliminar nota |

### Asistencias `/api/v1/asistencias`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/` | Registrar asistencia |
| `GET` | `/{id}` | Obtener por ID |
| `GET` | `/estudiante/{studentId}` | Asistencias del estudiante |
| `GET` | `/estudiante/{studentId}/fecha/{fecha}` | Asistencias por fecha |
| `GET` | `/estudiante/{studentId}/porcentaje` | % asistencia + riesgo repitencia |
| `PUT` | `/{id}` | Actualizar registro |
| `DELETE` | `/{id}` | Eliminar registro |

### Reportes `/api/v1/reportes`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/estudiante/{studentId}` | Reporte académico completo con alertas |

---

## Variables de Entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `DB_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/academico_db` |
| `DB_USERNAME` | Usuario de la BD | `postgres` |
| `DB_PASSWORD` | Contraseña de la BD | `postgres` |
| `EUREKA_URL` | URL del servidor Eureka | `http://localhost:8761/eureka` |

---

## Ejecución Local

### Pre-requisitos
- Java 17+
- Maven 3.9+
- PostgreSQL corriendo (o Supabase)

### Pasos

```bash
# Clonar y entrar al directorio
cd backend/ms-academico

# Compilar el proyecto
mvn clean package -DskipTests

# Ejecutar con variables de entorno
DB_URL=jdbc:postgresql://... DB_USERNAME=user DB_PASSWORD=pass mvn spring-boot:run
```

---

## Pruebas Unitarias y Cobertura

### Ejecutar todas las pruebas

```bash
mvn test
```

### Ver reporte de cobertura JaCoCo

```bash
mvn test jacoco:report
```

El reporte HTML se genera en:
```
target/site/jacoco/index.html
```

Abrí ese archivo en tu navegador para ver la cobertura detallada por clase y método.

### Configuración de umbral mínimo

El proyecto está configurado para **fallar el build** si la cobertura de líneas es menor al **60%**:

```xml
<!-- pom.xml — jacoco-maven-plugin -->
<limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.60</minimum>
</limit>
```

### Tests implementados

| Clase de Test | Cobertura principal |
|---------------|---------------------|
| `StudentTest` | Cálculo de promedios, asistencia, riesgo de repitencia |
| `GradeTest` | Validación de notas (1.0–7.0), aprobación |
| `GradeServiceTest` | Casos de uso: registrar, calcular promedio, eliminar |
| `AttendanceServiceTest` | Porcentaje de asistencia, detección de riesgo |
| `ReportServiceTest` | Generación de reportes y tipos de alerta |

---

## Docker

### Construir imagen

```bash
docker build -t ms-academico:1.0.0 .
```

### Ejecutar contenedor

```bash
docker run -p 8082:8082 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/academico_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  ms-academico:1.0.0
```

### Con Docker Compose (junto a otros microservicios)

```bash
# Desde la raíz del proyecto
docker-compose up ms-academico
```

---

## Green IT

Decisiones tomadas para minimizar el consumo de recursos:

- **HikariCP**: pool de conexiones limitado a 5 (suficiente para el MS)
- **`@Transactional(readOnly = true)`**: en consultas para optimizar caché de Hibernate
- **Multi-stage Dockerfile**: imagen final ~180MB (solo JRE Alpine, sin Maven ni código fuente)
- **`-XX:UseContainerSupport`**: JVM respeta los límites de memoria del contenedor
- **Queries específicas**: Spring Data JPA con métodos derivados — sin consultas genéricas
- **`default_batch_fetch_size`**: reduce el problema N+1 en colecciones

---

*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*  
*Docente: Alexis Jacob Jiménez Parada — Desarrollo Fullstack III*
