# 📚 MS-Académico — Microservicio de Gestión Académica

> Colegio Bernardo O'Higgins | Proyecto Fullstack III — Duoc UC

Microservicio de gestión de **cursos, asignaturas, matrículas, calificaciones, asistencia y reportes académicos** con notas en escala chilena (1.0–7.0).

---

## Índice

- [Arquitectura Hexagonal](#arquitectura-hexagonal)
- [Patrones de Diseño](#patrones-de-diseño)
- [Estructura del Proyecto](#estructura-del-proyecto)
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
│    │  CursoUseCase        │     CursoRepositoryPort    │         │
│    │  AsignaturaUseCase   │     AsignaturaRepositoryPort│        │
│    │  MatriculaUseCase    │     MatriculaRepositoryPort │        │
│    │  CalificacionUseCase │     CalificacionRepositoryPort│      │
│    │  AsistenciaUseCase   │     AsistenciaRepositoryPort│        │
│    │  ReporteUseCase      │                            │         │
│    │         │            │                            │         │
│    │  ┌──────▼──────────────────────────────────┐     │         │
│    │  │            DOMINIO                       │     │         │
│    │  │  Curso, Asignatura, Matricula             │     │         │
│    │  │  Calificacion, Asistencia                 │     │         │
│    │  │  AcademicReport (Value Object)           │     │         │
│    │  │  Lógica de negocio pura                  │     │         │
│    │  └──────────────────────────────────────────┘     │         │
│    └───────────────────────────────────────────────────┘         │
└──────────────────────────────────────────────────────────────────┘
```

### Capas y responsabilidades

| Capa | Paquete | Contenido |
|---|---|---|
| **Domain** | `domain.model` | Entidades puras: Curso, Asignatura, Matricula, Calificacion, Asistencia |
| **Domain** | `domain.exception` | AcademicoException, NotFoundException |
| **Application** | `application.port.in` | Puertos de entrada (UseCase interfaces) |
| **Application** | `application.port.out` | Puertos de salida (RepositoryPort interfaces) |
| **Application** | `application.service` | Casos de uso implementados con `@Service` |
| **Application** | `application.dto` | DTOs de respuesta (CursoResponse, AsignaturaResponse, etc.) |
| **Infrastructure** | `infrastructure.adapter.in.rest` | Controladores REST con DTOs |
| **Infrastructure** | `infrastructure.adapter.out.persistence` | JPA Entities, Spring Data repos, Adapter |
| **Infrastructure** | `infrastructure.config` | GlobalExceptionHandler, OpenApiConfig |
| **Infrastructure** | `infrastructure.factory` | AcademicReportFactory |

---

## Patrones de Diseño

### 1. Repository Pattern

Separa el acceso a datos de la lógica de negocio mediante puertos de salida:

```
CursoService → CursoRepositoryPort (interfaz) ← CursoPersistenceAdapter (JPA)
```

### 2. Factory Method — `AcademicReportFactory`

Centraliza la creación de `AcademicReport` determinando automáticamente el tipo de alerta:

```java
AcademicReport report = AcademicReportFactory.crear(estudiante, calificaciones, porcentajeAsistencia);
// Tipos de alerta:
//   SIN_ALERTA — Rendimiento y asistencia OK
//   ALERTA_RENDIMIENTO — Promedio bajo 4.0
//   ALERTA_ASISTENCIA — Asistencia bajo 85%
//   ALERTA_CRITICA — Ambas condiciones
```

### 3. DTO Pattern

Todas las respuestas de los controladores usan **DTOs inmutables** (Lombok `@Builder` / `@Getter`) para desacoplar la capa REST del dominio:

```
Controller → Service → Domain Entity → ResponseDTO → JSON
```

---

## Endpoints API

### Cursos `/api/v1/cursos`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/` | Listar todos los cursos |
| `POST` | `/crear` | Crear curso |

### Asignaturas `/api/v1/asignaturas`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/` | Listar todas las asignaturas |
| `POST` | `/crear` | Crear asignatura |

### Matrículas `/api/v1/matriculas`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/curso/{cursoId}/estudiantes` | Listar estudiantes del curso |
| `POST` | `/matricular` | Matricular estudiante en curso |

### Calificaciones `/api/v1/calificaciones`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/curso/{cursoId}/asignatura/{asignaturaId}` | Calificaciones del curso por asignatura |
| `GET` | `/estudiante/{usuarioUuid}` | Calificaciones del estudiante |
| `GET` | `/estudiante/{usuarioUuid}/asignatura/{asignaturaId}` | Nota por estudiante y asignatura |
| `PUT` | `/guardar` | Guardar o actualizar calificación |

### Asistencias `/api/v1/asistencias`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/{id}` | Obtener por ID |
| `GET` | `/estudiante/{studentId}` | Asistencias del estudiante |
| `GET` | `/estudiante/{studentId}/fecha/{fecha}` | Asistencia por fecha |
| `GET` | `/estudiante/{studentId}/porcentaje` | % asistencia + riesgo |
| `POST` | `/` | Registrar asistencia |
| `PUT` | `/{id}` | Actualizar registro |
| `DELETE` | `/{id}` | Eliminar registro |

### Reportes `/api/v1/reportes`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/estudiante/{usuarioUuid}` | Reporte académico con alertas |

### Asignación Docente `/api/v1/asignacion-docente`

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/` | Asignar docente a curso + asignatura |

### Ejemplo de uso

```bash
# Crear un curso
curl -X POST http://localhost:8080/api/v1/cursos/crear \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"nombre":"1° Básico A","nivel":"Básica"}'

# Obtener calificaciones
curl http://localhost:8080/api/v1/calificaciones/curso/1/asignatura/3 \
  -H "Authorization: Bearer <token>"
```

---

## Variables de Entorno

| Variable | Descripción | Default |
|---|---|---|
| `DB_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/colegio_db` |
| `DB_USERNAME` | Usuario BD | `colegio` |
| `DB_PASSWORD` | Contraseña BD | `colegio123` |
| `SPRING_PROFILES_ACTIVE` | Perfil (`dev`, `prod`) | `dev` |

---

## Ejecución Local

### Pre-requisitos

- Java 17+
- Maven 3.9+
- PostgreSQL (schema `academico_schema`)

### Compilar y ejecutar

```bash
cd backend/ms-academico

# Perfil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Perfil prod
DB_URL=jdbc:postgresql://localhost:5432/colegio_db \
DB_USERNAME=colegio \
DB_PASSWORD=colegio123 \
SPRING_PROFILES_ACTIVE=prod \
mvn spring-boot:run
```

### Swagger UI

Disponible en: `http://localhost:8082/swagger-ui.html`

---

## Pruebas Unitarias y Cobertura

### Ejecutar pruebas

```bash
mvn test
```

### Reporte de cobertura JaCoCo

```bash
mvn verify
# Reporte HTML en: target/site/jacoco/index.html
```

### Umbral mínimo

El build **falla** si la cobertura de líneas es menor al **60%**:

```xml
<limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.60</minimum>
</limit>
```

### Tests implementados (40 tests ✅)

| Clase de Test | Cobertura |
|---|---|
| `CursoServiceTest` | CRUD de cursos |
| `AsignaturaServiceTest` | CRUD de asignaturas |
| `MatriculaServiceTest` | Matriculación y listado |
| `CalificacionServiceTest` | Registro, promedios, escala 1.0–7.0 |
| `AsistenciaServiceTest` | Registro, porcentaje, umbral 85% |
| `ReporteServiceTest` | Generación de reportes y alertas |
| `AcademicReportFactoryTest` | Factory con tipos de alerta |
| `DomainModelTest` | Entidades de dominio |

---

## Docker

### Construir imagen

```bash
docker build -t ms-academico:1.0.0 .
```

### Ejecutar contenedor

```bash
docker run -p 8082:8082 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/colegio_db \
  -e DB_USERNAME=colegio \
  -e DB_PASSWORD=colegio123 \
  ms-academico:1.0.0
```

### Con Docker Compose

```bash
docker compose -f docker-compose.test.yml up --build ms-academico
```

---

## Estructura del Proyecto

```
ms-academico/
├── src/
│   ├── main/java/cl/duoc/colegio/academico/
│   │   ├── MsAcademicoApplication.java
│   │   ├── domain/
│   │   │   ├── model/          ← Curso, Asignatura, Matricula, Calificacion, Asistencia
│   │   │   └── exception/      ← AcademicoException, NotFoundException
│   │   ├── application/
│   │   │   ├── port/
│   │   │   │   ├── in/         ← CursoUseCase, AsignaturaUseCase, MatriculaUseCase, etc.
│   │   │   │   └── out/        ← CursoRepositoryPort, AsignaturaRepositoryPort, etc.
│   │   │   ├── service/        ← CursoService, AsignaturaService, CalificacionService, etc.
│   │   │   └── dto/            ← CursoResponse, AsignaturaResponse, CalificacionResponse, etc.
│   │   └── infrastructure/
│   │       ├── adapter/
│   │       │   ├── in/rest/    ← CursoController, AsignaturaController, etc.
│   │       │   └── out/persistence/ ← JPA Entities, Repos, Adapters
│   │       ├── config/         ← GlobalExceptionHandler, OpenApiConfig
│   │       └── factory/        ← AcademicReportFactory
│   ├── resources/
│   │   ├── application.yml
│   │   └── application-prod.yml
│   └── test/
├── Dockerfile
├── pom.xml
└── README.md
```

---

*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*
*Docente: Alexis Jacob Jiménez Parada — Desarrollo Fullstack III*