# 🎓 Plataforma Educativa Colegio Bernardo O'Higgins

Sistema web moderno basado en **Microservicios + Arquitectura Hexagonal**, diseñado para digitalizar la gestión académica, asistencia y comunicación escolar del Colegio Bernardo O'Higgins de Coquimbo.

---

## 📌 Descripción del Proyecto

Este proyecto nace como solución a los problemas administrativos y académicos actuales del establecimiento, tales como:

- Dependencia de libro de clases físico.
- Dificultad para revisar historial académico.
- Procesos lentos para reportes institucionales.
- Comunicación deficiente entre docentes, alumnos y apoderados.
- Falta de herramientas digitales modernas.

La plataforma permite una administración centralizada, segura y escalable.

---

## 📊 Estado del Proyecto

| Componente | Estado | Puerto | Descripción |
|---|---|---|---|
| **MS-Académico** | ✅ Implementado | `8082` | Gestión de notas, asistencias y reportes |
| **MS-Usuario** | ✅ Implementado | `8083` | Autenticación JWT y autorización por roles |
| **MS-Comunicaciones** | ✅ Implementado | `8085` | Mensajería y notificaciones |
| **API Gateway** | ✅ Implementado | `8080` | Enrutamiento, JWT, Circuit Breaker |
| **Frontend React** | ✅ Implementado | `5173` | Vite + React Router + Axios |
| **MS-Asistencia** | 🔲 Pendiente | `8084` | Módulo de asistencia dedicado |
| **MS-BFF** | 🔲 Pendiente | `8090` | Backend For Frontend |

---

## 🏗️ Arquitectura del Sistema

```
┌──────────────────────────────────────────────────────────────┐
│                      FRONTEND (React + Vite)                 │
│  auth · gestion-academica · comunicaciones · admin           │
└──────────────────────────┬───────────────────────────────────┘
                           │ HTTP :5173 → :8080
                           ▼
┌──────────────────────────────────────────────────────────────┐
│                       API GATEWAY :8080                      │
│  JwtValidationFilter · CircuitBreaker · RouteLocator         │
└───┬──────────────────┬──────────────────────┬───────────────┘
    │ /api/v1/auth/**  │ /api/v1/gestion/**   │ /api/v1/comunicaciones/**
    ▼                  ▼                      ▼
┌──────────┐   ┌──────────────┐   ┌──────────────────────┐
│MS-Usuario│   │ MS-Académico │   │  MS-Comunicaciones   │
│  :8083   │   │    :8082     │   │       :8085          │
└────┬─────┘   └──────┬───────┘   └──────────┬───────────┘
     │                │                       │
┌────▼────┐    ┌──────▼──────┐        ┌──────▼──────┐
│ usuario │    │ academico   │        │notificacion │
│   _db   │    │    _db      │        │    _db      │
│ :5433   │    │    :5432    │        │    :5434    │
└─────────┘    └─────────────┘        └─────────────┘
```

### Patrones de arquitectura usados

| Patrón | Aplicado en |
|--------|-------------|
| **Hexagonal (Ports & Adapters)** | MS-Académico, MS-Usuario |
| **Strategy** | MS-Usuario — autorización por rol |
| **Factory Method** | MS-Usuario (`UserStrategyFactory`), MS-Académico (`AcademicReportFactory`) |
| **Repository** | Todos los microservicios |
| **Circuit Breaker** | API Gateway (Resilience4j) |
| **Database per Service** | Cada MS tiene su propia BD |

---

## ⚙️ Microservicios

### 🔐 MS-Usuario — Autenticación y Autorización

**Puerto:** `8083` | **BD:** `usuario_db` (:5433)

Gestiona la identidad de todos los actores del sistema mediante JWT con claims de rol.

**Roles disponibles:**

| Rol | Permisos | Restricción clave |
|-----|----------|-------------------|
| `ADMIN` | Acceso total — GET, POST, PUT, DELETE | Ninguna |
| `DOCENTE` | Notas, asistencias, estudiantes, cursos | Solo sus cursos asignados |
| `APODERADO` | Solo lectura — notas y asistencias | Solo datos de su pupilo (`pupiloId` en JWT) |
| `ESTUDIANTE` | Solo lectura — sus propias notas y asistencias | Solo sus datos (`estudianteId` en JWT) |

**Endpoints:**
- `POST /api/v1/auth/login` — Login con email + password
- `POST /api/v1/auth/register` — Registro de nuevo usuario
- `GET /api/v1/auth/health` — Health check

---

### 📚 MS-Académico — Gestión Académica

**Puerto:** `8082` | **BD:** `academico_db` (:5432)

Gestión completa del historial académico: estudiantes, notas, asistencias y reportes con alertas automáticas.

**Endpoints principales:**
- `GET/POST /api/v1/estudiantes` — CRUD de estudiantes
- `GET/POST /api/v1/notas` — Registro y consulta de notas
- `GET /api/v1/notas/estudiante/{id}/promedio` — Cálculo de promedios
- `GET/POST /api/v1/asistencias` — Registro de asistencia
- `GET /api/v1/asistencias/estudiante/{id}/porcentaje` — % asistencia + riesgo
- `GET /api/v1/reportes/estudiante/{id}` — Reporte académico completo con alertas

---

### 📩 MS-Comunicaciones — Mensajería

**Puerto:** `8085` | **BD:** `notificaciones_db`

Mensajería oficial entre docentes y apoderados, notificaciones automáticas del sistema.

**Endpoints principales:**
- `POST /api/notificaciones` — Enviar notificación
- `GET /api/notificaciones` — Listar notificaciones

---

## 💻 Stack Tecnológico

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
| PostgreSQL | 16 | Base de datos |
| JUnit 5 + Mockito | Incluido en SB | Testing |
| JaCoCo | 0.8.11 | Cobertura de código (mín. 60%) |

### Frontend

| Tecnología | Versión | Uso |
|---|---|---|
| React | 18.x | Framework UI |
| Vite | 5.x | Bundler |
| React Router | 6.x | Enrutamiento |
| Axios | 1.x | Cliente HTTP |
| CSS Modules | — | Estilos por feature |

### DevOps

| Tecnología | Uso |
|---|---|
| Docker + Docker Compose | Contenedorización y orquestación local |
| Dockerfile multi-stage | Imágenes optimizadas (JRE Alpine) |
| Railway | Despliegue en nube (backend) |
| Netlify | Despliegue en nube (frontend) |

---

## 🚀 Inicio Rápido

### Requisitos

- Docker Desktop
- Java 17+
- Maven 3.9+
- Node.js 20+

### Opción A — Stack completo con Docker Compose

```bash
# Clonar el repositorio
git clone https://github.com/yaquelin2305/colegio-bernardo-ohiggins-platform.git
cd colegio-bernardo-ohiggins-platform

# Levantar todo (PostgreSQL + MS-Académico + MS-Usuario + API Gateway)
docker compose up --build

# Verificar que todo levantó
curl http://localhost:8080/actuator/health
curl http://localhost:8083/api/v1/auth/health
curl http://localhost:8082/actuator/health
```

### Opción B — Desarrollo local (sin Docker)

```bash
# 1. MS-Académico
cd backend/ms-academico
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 2. MS-Usuario
cd backend/ms-usuario
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. API Gateway
cd backend/api-gateway
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local

# 4. Frontend
cd frontend
npm install
npm run dev
```

### Primer login

Una vez levantado el stack, el sistema tiene un usuario ADMIN inicial:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@colegio-ohiggins.cl","password":"Admin1234!"}'
```

---

## 📁 Estructura del Repositorio

```
colegio-bernardo-ohiggins-platform/
├── backend/
│   ├── api-gateway/          ← Spring Cloud Gateway :8080
│   ├── ms-academico/         ← Gestión académica :8082
│   ├── ms-usuario/           ← Auth y autorización :8083
│   ├── ms-comunicaciones/    ← Mensajería :8085
│   ├── ms-asistencia/        ← (pendiente)
│   └── ms-bff/               ← (pendiente)
├── frontend/                 ← React + Vite :5173
│   └── src/
│       ├── features/
│       │   ├── auth/         ← Login, Registro
│       │   ├── gestion-academica/ ← Dashboard, notas
│       │   └── comunicaciones/   ← Mensajería
│       ├── core/             ← Axios, AuthContext, constantes
│       └── shared/           ← Layout, Header, Sidebar
├── docs/
│   └── GUIA-PRUEBAS.md       ← Guía completa de pruebas con CURLs
├── docker/
│   └── init-db.sql           ← Script de inicialización BD académica
└── docker-compose.yml        ← Orquestación local completa
```

---

## 🔐 Seguridad

- **JWT** firmado con HMAC-SHA256 (jjwt 0.11.5)
- **Claims de rol** en el token: recursos permitidos, tipo de operaciones, IDs de filtrado
- **Filtrado por identidad**: `pupiloId` y `estudianteId` en el JWT garantizan que un apoderado/estudiante SOLO vea sus propios datos
- **BCrypt** (strength 12) para hash de contraseñas
- **Circuit Breaker** en el Gateway: protege todos los MSs downstream
- **SecurityConfig separado** por perfil: `dev` (sin JWT) y `prod` (JWT obligatorio)
- **Protección de datos de menores** conforme a Ley 19.628

---

## 🌱 Green IT / Sostenibilidad

- **Dockerfiles multi-stage**: imagen final ~180MB (solo JRE Alpine, sin Maven)
- **`-XX:UseContainerSupport`**: JVM respeta límites de memoria del contenedor
- **HikariCP con pool limitado**: no desperdiciar conexiones de BD
- **`@Transactional(readOnly=true)`**: optimiza caché de Hibernate en lecturas
- **Circuit Breaker**: evita cascadas de fallas que consumen recursos en loop
- **Rate Limiting** en Gateway: evita saturar MSs con peticiones abusivas

---

## 👨‍💻 Equipo

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

`Java 17` `Spring Boot 3` `React` `Vite` `JWT` `Docker` `Railway` `PostgreSQL` `Hexagonal Architecture`
