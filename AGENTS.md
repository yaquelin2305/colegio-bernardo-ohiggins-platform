# AGENTS.md — Colegio Bernardo O'Higgins Platform

## Project Overview

Plataforma educativa basada en **microservicios + arquitectura hexagonal** para digitalizar la gestión académica, asistencia y comunicación del Colegio Bernardo O'Higgins de Coquimbo.

- **Repositorio:** https://github.com/yaquelin2305/colegio-bernardo-ohiggins-platform
- **Asignatura:** Desarrollo Fullstack III — Duoc UC
- **Equipo:** Yaquelin Rugel, Yeider Catari, Victor Barrera, Maria José Velázquez, Eliezer Carrasco

## Tech Stack

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Lenguaje | Java | 17 |
| Framework | Spring Boot | 3.2.5 |
| Cloud | Spring Cloud | 2023.0.1 |
| Gateway | Spring Cloud Gateway (WebFlux/Netty) | MVC + reactive |
| Seguridad | Spring Security + jjwt | 6.x / 0.11.5 |
| Persistencia | Spring Data JPA + Hibernate | 3.x |
| Base de datos | PostgreSQL | 16 (1 instancia, 2 schemas) |
| Service Discovery | Netflix Eureka | (dev/docker sin Eureka) |
| Resilience | Resilience4j (Circuit Breaker) | incluido en SC |
| Testing | JUnit 5 + Mockito + AssertJ | |
| Coverage | JaCoCo | 0.8.11 (mín 60% líneas) |
| Frontend | React + Vite + React Router + Axios | 18.x / 5.x |
| Contenedores | Docker + Docker Compose | multi-stage builds |
| Build | Maven | 3.9+ |

## Architecture

```
Frontend (React/Vite :5173)
        │
        ▼
API Gateway (Spring Cloud Gateway :8080)
  ├── JwtValidationFilter (JWT + RBAC global)
  ├── CircuitBreaker por microservicio
  └── CORS centralizado
        │
   ┌────┼────────────┐
   ▼    ▼            ▼
MS-Usuario  MS-Academico  MS-Comunicaciones
 :8083       :8082          :8085 (scaffold)
   │          │
   └────┬─────┘
        ▼
  PostgreSQL :5432 (colegio_db)
    ├── users_schema
    └── academico_schema
```

## Directory Structure

```
backend/
├── api-gateway/          # Spring Cloud Gateway :8080
├── ms-usuario/           # Auth + RBAC :8083
├── ms-academico/         # Gestión académica :8082 (alias "ms-gestion")
├── ms-bff/               # Backend For Frontend :8084 (parcial)
├── ms-comunicaciones/    # Mensajería (scaffold)
├── ms-asistencia/        # Asistencia (scaffold)
└── ms-users/             # Duplicado placeholder (ignorar, usar ms-usuario)

frontend/                 # React + Vite
  └── src/
      ├── features/       # auth, gestion-academica, usuarios, comunicaciones
      ├── core/           # Axios, AuthContext, constantes
      └── shared/         # Layout, Header, Sidebar

docker/
  └── init.sql            # Crea users_schema + academico_schema + seed data
docker-compose.yml        # Orquestación completa
```

## Hexagonal Architecture Convention

Cada microservicio implementado sigue **Ports & Adapters** estrictamente:

```
domain/
  ├── model/         # Entidades puras (sin anotaciones Spring/JPA)
  ├── port/
  │   ├── in/        # Puertos de entrada (UseCase interfaces)
  │   └── out/       # Puertos de salida (RepositoryPort, etc.)
  └── exception/     # Excepciones de dominio

application/
  ├── usecase/       # Implementaciones de casos de uso (@Service)
  ├── service/       # Servicios de aplicación (ms-academico)
  ├── strategy/      # Patrón Strategy (ms-usuario: autorización por rol)
  ├── factory/       # Factories
  └── dto/           # DTOs de aplicación

infrastructure/
  ├── adapter/
  │   ├── in/rest/   # Controladores REST + DTOs HTTP + ExceptionHandler
  │   └── out/
  │       ├── persistence/  # JPA entities, Spring Data repos, adapters
  │       └── security/     # JWT, BCrypt adapters
  └── config/        # SecurityConfig, OpenApi, etc.
```

**Regla de dependencia:** infrastructure → application → domain. Domain no depende de nada externo.

## Key Design Patterns

| Patrón | Dónde | Propósito |
|--------|-------|-----------|
| Hexagonal (Ports & Adapters) | ms-usuario, ms-academico | Independencia del framework, testabilidad |
| Strategy | ms-usuario `AuthorizationStrategy` | Autorización por rol sin if/else |
| Factory Method | `UserStrategyFactory`, `AcademicReportFactory` | Creación centralizada |
| Repository | `*RepositoryPort` interfaces | Abstracción de persistencia |
| Circuit Breaker | api-gateway (Resilience4j) | Tolerancia a fallos |
| DTO (Java Records) | Todos los MS | Transporte inmutable de datos |

## API Gateway Details

- **Puerto:** 8080
- **Seguridad en 2 capas:** Spring Security (coarse) + JwtValidationFilter (RBAC fino, `Ordered.HIGHEST_PRECEDENCE`)
- **Perfiles:**
  - `dev`: Sin seguridad JWT, rutas directas a localhost
  - `docker`: Sin Eureka, URIs estáticas a contenedores, JwtValidationFilter activo
  - `prod`: JWT + RBAC completo, Eureka
  - `local`: Sin Eureka, rutas directas configurables
- **Circuit Breakers:** academicoCB, usuarioCB, comunicacionesCB (sliding-window=10, failure-rate=50%, wait=10s, timelimiter=8s)
- **Fallback:** RFC 7807 Problem Details en `/fallback/**`
- **RBAC en Gateway:**
  - `/api/v1/admin/**` → ADMIN
  - `/api/v1/asignacion-docente/**` → ADMIN
  - `/api/v1/cursos/**`, `/api/v1/asignaturas/**`, `/api/v1/matriculas/**` → ADMIN o DOCENTE
  - `/api/bff/boletin/{uuid}` → ADMIN, DOCENTE, APODERADO, ESTUDIANTE (self)
  - `/api/v1/auth/login`, `/api/v1/auth/health` → público

## MS-Usuario (:8083) — Auth

- **Schema:** `users_schema`
- **Login por RUT** (formato chileno: `12345678-9`)
- **JWT:** HMAC-SHA256, TTL 24h. Claims: sub=RUT, userId, email, nombre, role, recursos, soloLectura
- **Seguridad:** Modelo trust-the-gateway en prod (todo `/api/**` permitido). El RBAC lo aplica el Gateway via `JwtValidationFilter`.
- **Roles:** ADMIN, DOCENTE, APODERADO, ESTUDIANTE
- **Password:** BCrypt strength 12
- **Soft delete:** `activo=false` (preserva integridad referencial)
- **Endpoints principales:**
  - `POST /api/v1/auth/login`
  - `GET /api/v1/auth/health`
  - `POST /api/v1/admin/crear`
  - `GET /api/v1/admin/listar/{rol}`
  - `PUT /api/v1/admin/actualizar/{id}`
  - `DELETE /api/v1/admin/eliminar/{id}`

## MS-Academico (:8082) — Gestión

- **Schema:** `academico_schema`
- **DDL:** `validate` (default), `update` (dev/prod)
- **Entidades:** Student, Grade, Attendance, Curso, Asignatura, Matricula, AsignacionDocente, AcademicReport
- **Notas:** Escala chilena 1.0–7.0, tipos: PRUEBA/TAREA/EXAMEN/TRABAJO
- **Umbral asistencia:** 85% mínimo
- **Reportes:** AcademicReport con alertas (SIN_ALERTA, ALERTA_RENDIMIENTO, ALERTA_ASISTENCIA, ALERTA_CRITICA)
- **Endpoints principales:**
  - Cursos: `GET/POST /api/v1/cursos`
  - Asignaturas: `GET/POST /api/v1/asignaturas`
  - Matrículas: `GET/POST /api/v1/matriculas`
  - Calificaciones: `GET/PUT /api/v1/calificaciones`
  - Asistencia: `GET/POST /api/v1/asistencias`
  - Reportes: `GET /api/v1/reportes/estudiante/{id}`
  - Asignación docente: `POST /api/v1/asignacion-docente`

## Build & Run

### Docker Compose (recomendado)
```bash
docker compose up --build
```

### Desarrollo local
```bash
# MS-Usuario (:8083)
cd backend/ms-usuario && mvn spring-boot:run -Dspring-boot.run.profiles=dev

# MS-Academico (:8082)
cd backend/ms-academico && mvn spring-boot:run -Dspring-boot.run.profiles=dev

# API Gateway (:8080)
cd backend/api-gateway && mvn spring-boot:run -Dspring-boot.run.profiles=dev,local

# Frontend (:5173)
cd frontend && npm install && npm run dev
```

### Tests
```bash
mvn test        # Ejecutar tests
mvn verify      # Tests + JaCoCo coverage (mín 60%)
```

## Code Conventions

- **Idioma:** Código en español (clases, métodos, variables)
- **Inyección:** Solo por constructor (sin `@Autowired` explícito)
- **Lombok:** `@Getter`, `@Builder` en entidades y DTOs, `@Slf4j` para logging
- **DTOs:** Java `record` en ms-usuario, Lombok `@Data`/`@Builder` en ms-academico
- **Excepciones:** Jerarquía `DomainException → EspecificaException`. Handler global con ProblemDetail (RFC 7807)
- **Transacciones:** `@Transactional` en services, `readOnly=true` en lecturas
- **Validación:** Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Email`, etc.)
- **Logging:** SLF4J via Lombok `@Slf4j`, niveles por perfil (DEBUG dev, WARN prod)

## Known Issues

1. ~~`CalificacionesController.java` en ms-academico tiene **dos definiciones de clase**~~ — CORREGIDO
2. ~~`JwtTokenAdapterTest` llama a `extraerEmail()` pero el adapter solo tiene `extraerRut()`~~ — CORREGIDO
3. ~~`JwtTokenAdapterTest` usa constructor de 2 args, pero `JwtTokenAdapter` requiere 3~~ — CORREGIDO
4. ~~ms-bff no propaga headers `X-User-Id`/`X-User-Role` a Feign clients~~ — CORREGIDO (FeignConfig con RequestInterceptor)
5. ~~ms-bff tiene campos en DTOs (`totalCursos`, `totalAsignaturas`) no poblados~~ — CORREGIDO
6. ~~Refresh Token eliminado de ms-usuario~~ — REMOVIDO (no requerido). Se eliminaron endpoints `/auth/refresh` y `/auth/logout`, modelo RefreshToken, entidad JPA, tabla SQL y adaptadores.
7. ~~ms-usuario `SecurityConfigProd` bloqueaba endpoints admin~~ — CORREGIDO (trust-the-gateway: todo `/api/**` permitAll, RBAC en Gateway)
8. ~~`application-local.yml` del Gateway sin ruta `/api/v1/admin/**`~~ — CORREGIDO
9. ms-comunicaciones, ms-asistencia, ms-users son solo scaffolds vacíos
10. ms-bff `promedioGeneralInstitucion` y `porcentajeAsistencia` requieren endpoints nuevos en MS-Académico y MS-Comunicaciones
11. ms-bff `nombreCompleto` en boletín se obtiene filtrando lista ESTUDIANTE — un endpoint `GET /api/v1/admin/{id}` sería más eficiente
12. MapStruct declarado en ms-usuario pero no usado
13. Sin OpenAPI/Swagger en ms-usuario
14. Sin tests para `RegistroUseCaseImpl` en ms-usuario
15. CorsConfig.java del Gateway conflictúa con YAML globalcors (`allowedOriginPatterns: ["*"]` con `allowCredentials: true`)

## Test Status

| Microservicio | Tests | Estado |
|--------------|-------|--------|
| ms-usuario | 37 | ✅ Todos pasan |
| ms-academico | 40 | ✅ Todos pasan |
| ms-bff | 0 | Sin tests (pendiente) |
| api-gateway | 0 | Sin tests (pendiente) |
