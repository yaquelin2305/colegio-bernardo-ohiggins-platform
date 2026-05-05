# Plataforma Educativa Colegio Bernardo O'Higgins

Sistema web moderno basado en **Microservicios + Arquitectura Hexagonal**, diseñado para digitalizar la gestión académica, asistencia y comunicación escolar del Colegio Bernardo O'Higgins de Coquimbo.

---

## Descripción del Proyecto

Este proyecto nace como solución a los problemas administrativos y académicos actuales del establecimiento, tales como:

- Dependencia de libro de clases físico.
- Dificultad para revisar historial académico.
- Procesos lentos para reportes institucionales.
- Comunicación deficiente entre docentes, alumnos y apoderados.
- Falta de herramientas digitales modernas.

La plataforma permite una administración centralizada, segura y escalable.

---

## Estado del Proyecto

| Componente | Estado | Puerto | Descripción |
|---|---|---|---|
| **MS-Académico** | ✅ Implementado | `8082` | Gestión de cursos, asignaturas, matrículas, calificaciones |
| **MS-Usuario** | ✅ Implementado | `8083` | Autenticación JWT + Refresh Token + RBAC por roles |
| **MS-Comunicaciones** | ✅ Implementado | `8085` | Mensajería y notificaciones |
| **API Gateway** | ✅ Implementado | `8080` | Enrutamiento, JWT, RBAC, CORS, Circuit Breaker |
| **Frontend React** | ✅ Implementado | `5173` | Vite + React Router + Axios |
| **MS-BFF** | 🔲 Pendiente | `8084` | Backend For Frontend |

---

## Arquitectura del Sistema

```
┌──────────────────────────────────────────────────────────────┐
│                      FRONTEND (React + Vite)                 │
│  auth · gestion-academica · comunicaciones · admin           │
└──────────────────────────┬───────────────────────────────────┘
                           │ HTTP :5173 → :8080
                           ▼
┌──────────────────────────────────────────────────────────────┐
│                       API GATEWAY :8080                      │
│  JwtValidationFilter · RBAC · CircuitBreaker · CORS          │
└───┬──────────────────┬──────────────────────┬───────────────┘
    │ /api/v1/auth/**  │ /api/v1/cursos/**    │ /api/v1/comunicaciones/**
    │ /api/v1/admin/** │ /api/v1/matriculas/**│
    ▼                  │ /api/v1/calificac./**│
┌──────────┐           ▼                      ▼
│MS-Usuario│   ┌──────────────┐   ┌──────────────────────┐
│  :8083   │   │ MS-Académico │   │  MS-Comunicaciones   │
└────┬─────┘   │    :8082     │   │       :8085          │
     │         └──────┬───────┘   └──────────────────────┘
     │                │
     └───────┬─────────┘
             ▼
   ┌─────────────────────┐
   │  PostgreSQL :5432   │
   │  colegio_db         │
   │  users_schema       │
   │  academico_schema   │
   └─────────────────────┘
```

### Patrones de arquitectura usados

| Patrón | Aplicado en |
|--------|-------------|
| **Hexagonal (Ports & Adapters)** | MS-Académico, MS-Usuario |
| **Strategy** | MS-Usuario — autorización por rol |
| **Repository** | Todos los microservicios |
| **Circuit Breaker** | API Gateway (Resilience4j) |
| **One BD — schemas separados** | colegio_db → users_schema + academico_schema |

---

## Microservicios

### MS-Usuario — Autenticación y Autorización

**Puerto:** `8083` | **Schema:** `users_schema`

Gestiona la identidad de todos los actores del sistema mediante JWT firmado con HMAC-SHA256.

**Roles disponibles:**

| Rol | Permisos |
|-----|----------|
| `ADMIN` | Acceso total — gestión de usuarios, cursos, asignaciones |
| `DOCENTE` | Registro de notas, visualización de cursos asignados |
| `APODERADO` | Solo lectura — boletín de notas de su pupilo |
| `ESTUDIANTE` | Solo lectura — su propio boletín de notas |

**Endpoints:**
- `POST /api/v1/auth/login` — Login con RUT + password → `{ accessToken, refreshToken }`
- `POST /api/v1/auth/refresh` — Renovar accessToken con refreshToken (rotación one-time-use)
- `POST /api/v1/auth/logout` — Revocar refreshToken
- `POST /api/v1/admin/crear` — Crear usuario (ADMIN)
- `GET  /api/v1/admin/listar/{rol}` — Listar usuarios por rol (ADMIN)
- `PUT  /api/v1/admin/actualizar/{id}` — Actualizar usuario (ADMIN)
- `DELETE /api/v1/admin/eliminar/{id}` — Soft delete de usuario (ADMIN)

---

### MS-Académico — Gestión Académica

**Puerto:** `8082` | **Schema:** `academico_schema`

Gestión completa de cursos, asignaturas, matrículas, asignación de docentes y calificaciones.

**Endpoints principales:**
- `GET/POST /api/v1/cursos` — CRUD de cursos
- `GET/POST /api/v1/asignaturas` — CRUD de asignaturas
- `GET/POST /api/v1/asignacion-docente` — Asignar docente a curso+asignatura
- `POST /api/v1/matriculas` — Matricular estudiante en curso
- `GET  /api/v1/matriculas/curso/{cursoId}/estudiantes` — Estudiantes de un curso
- `GET  /api/v1/calificaciones/curso/{cursoId}/asignatura/{asignaturaId}` — Notas por curso y asignatura
- `POST /api/v1/calificaciones/curso/{cursoId}/asignatura/{asignaturaId}` — Guardar notas

---

### MS-Comunicaciones — Mensajería

**Puerto:** `8085`

Mensajería oficial entre docentes y apoderados, notificaciones automáticas del sistema.

---

## Stack Tecnológico

### Backend

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.2.5 | Framework base |
| Spring Security | 6.x | Seguridad |
| Spring Cloud Gateway | 2023.0.1 | API Gateway reactivo |
| Spring Data JPA | 3.x | Persistencia |
| jjwt | 0.11.5 | Generación/validación JWT |
| Resilience4j | Incluido en SC | Circuit Breaker |
| PostgreSQL | 16 | Base de datos (1 instancia, 2 schemas) |
| JUnit 5 + Mockito | Incluido en SB | Testing |
| JaCoCo | 0.8.11 | Cobertura de código (mín. 60%) |

### Frontend

| Tecnología | Versión | Uso |
|---|---|---|
| React | 18.x | Framework UI |
| Vite | 5.x | Bundler |
| React Router | 6.x | Enrutamiento |
| Axios | 1.x | Cliente HTTP |

### DevOps

| Tecnología | Uso |
|---|---|
| Docker + Docker Compose | Contenedorización y orquestación local |
| Dockerfile multi-stage | Imágenes optimizadas (JRE Alpine + Nginx) |

---

## Inicio Rápido

### Requisitos

- Docker Desktop
- Java 17+
- Maven 3.9+
- Node.js 20+

### Opción A — Stack completo con Docker Compose

```bash
git clone https://github.com/yaquelin2305/colegio-bernardo-ohiggins-platform.git
cd colegio-bernardo-ohiggins-platform

docker compose up --build
```

### Primer login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"rut":"12345678-9","password":"Admin1234!"}'
```

### Opción B — Desarrollo local (sin Docker)

```bash
# MS-Usuario
cd backend/ms-usuario
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# MS-Académico
cd backend/ms-academico
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# API Gateway
cd backend/api-gateway
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

---

## Estructura del Repositorio

```
colegio-bernardo-ohiggins-platform/
├── backend/
│   ├── api-gateway/          ← Spring Cloud Gateway :8080
│   ├── ms-academico/         ← Gestión académica :8082
│   ├── ms-usuario/           ← Auth y autorización :8083
│   ├── ms-comunicaciones/    ← Mensajería :8085
│   └── ms-bff/               ← (pendiente)
├── frontend/                 ← React + Vite :5173
│   └── src/
│       ├── features/
│       │   ├── auth/              ← Login
│       │   ├── gestion-academica/ ← Dashboard, notas, cursos
│       │   ├── usuarios/          ← Gestión admin de usuarios
│       │   └── comunicaciones/    ← Mensajería
│       ├── core/             ← Axios, AuthContext, constantes
│       └── shared/           ← Layout, Header, Sidebar
├── docker/
│   └── init.sql              ← Crea users_schema + academico_schema
└── docker-compose.yml        ← Orquestación local completa
```

---

## Seguridad

- **JWT** firmado con HMAC-SHA256 (`sub` = RUT del usuario)
- **Refresh Token** con rotación one-time-use (TTL 7 días, almacenado en BD)
- **RBAC en Gateway**: `JwtValidationFilter` valida token y rol antes de rutear
- **BCrypt** (strength 12) para hash de contraseñas
- **Circuit Breaker** en el Gateway: protege todos los MSs downstream
- **CORS** configurado en Gateway: `localhost:5173` + variable `FRONTEND_URL`

---

## Equipo

| Integrante | GitHub |
|---|---|
| Yaquelin Rugel | [@yaquelin2305](https://github.com/yaquelin2305) |
| Yeider Catari | — |
| Victor Barrera | — |
| Maria José Velázquez | — |
| Eliezer Carrasco | — |

**Asignatura:** Desarrollo Fullstack III
**Docente:** Alexis Jacob Jiménez Parada
**Institución:** Duoc UC

---

`Java 17` `Spring Boot 3` `React` `Vite` `JWT` `Refresh Token` `Docker` `PostgreSQL` `Hexagonal Architecture`
