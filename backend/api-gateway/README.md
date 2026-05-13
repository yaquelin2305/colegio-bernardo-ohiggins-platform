# 🌐 API Gateway — Colegio Bernardo O'Higgins

> Punto de entrada único a la plataforma de microservicios | Proyecto Fullstack III — Duoc UC

## Índice

- [¿Qué hace el Gateway?](#qué-hace-el-gateway)
- [Rutas Configuradas](#rutas-configuradas)
- [Seguridad JWT + RBAC](#seguridad-jwt--rbac)
- [Circuit Breaker — Resilience4j](#circuit-breaker--resilience4j)
- [Perfiles de Ejecución](#perfiles-de-ejecución)
- [Variables de Entorno](#variables-de-entorno)
- [Ejecución Local](#ejecución-local)
- [Docker](#docker)

---

## ¿Qué hace el Gateway?

El API Gateway actúa como **fachada inteligente** frente a todos los microservicios:

```
Frontend / Cliente (:3000)
        │
        ▼ :8080
  ┌─────────────────────────────────────────────┐
  │              API GATEWAY                     │
  │                                              │
  │  1. JwtValidationFilter   ← verifica token   │
  │  2. RBAC por ruta         ← autoriza acceso  │
  │  3. CircuitBreaker        ← protege el MS    │
  │  4. CORS centralizado     ← headers globales │
  └──────────────┬──────────────────────────────┘
                 │
        ┌────────┼───────────┬────────────────┐
        ▼        ▼           ▼                ▼
   MS-Usuario  MS-Academico  MS-BFF  MS-Comunicaciones
    :8083       :8082        :8084       :8085
```

### Responsabilidades

| Responsabilidad | Componente |
|---|---|
| Autenticación JWT | `JwtValidationFilter` |
| Autorización RBAC | `JwtValidationFilter` (ADMIN, DOCENTE, APODERADO, ESTUDIANTE) |
| Tolerancia a fallos | Resilience4j Circuit Breaker |
| Degradación controlada | `FallbackController` (RFC 7807 Problem Details) |
| CORS global | `application.yml` + `CorsConfig` |
| Propagación de identidad | Headers `X-User-Id`, `X-User-Role` |

---

## Rutas Configuradas

### Perfil `dev` / `local` (sin Eureka)

| Ruta Pública | Destino | Circuit Breaker |
|---|---|---|
| `/api/v1/auth/**` | `ms-usuario:8083` | usuarioCB |
| `/api/v1/admin/**` | `ms-usuario:8083` | usuarioCB |
| `/api/v1/cursos/**` | `ms-academico:8082` | academicoCB |
| `/api/v1/asignaturas/**` | `ms-academico:8082` | academicoCB |
| `/api/v1/matriculas/**` | `ms-academico:8082` | academicoCB |
| `/api/v1/calificaciones/**` | `ms-academico:8082` | academicoCB |
| `/api/v1/asignacion-docente/**` | `ms-academico:8082` | academicoCB |
| `/api/bff/**` | `ms-bff:8084` | bffCB |
| `/api/v1/comunicaciones/**` | `ms-comunicaciones:8085` | comunicacionesCB |
| `/api/v1/asistencias/**` | `ms-comunicaciones:8085` | comunicacionesCB |
| `/fallback/**` | `FallbackController` | — |

### Perfil `docker` (Docker Compose)

Igual rutas que `local`, pero con URIs estáticas a nombres de contenedor:
```
ms-usuario:8083, ms-academico:8082, ms-bff:8084, ms-comunicaciones:8085
```

---

## Seguridad JWT + RBAC

### Flujo de autenticación

```
1. POST /api/v1/auth/login  →  bypass (PUBLIC_PATHS)
2. MS-Usuario valida RUT + BCrypt  →  genera JWT
3. Frontend almacena JWT en localStorage
4. Todas las demás peticiones incluyen: Authorization: Bearer <token>
5. JwtValidationFilter valida firma HMAC-SHA256 y expiración
6. RBAC: verifica que el rol tenga acceso a la ruta
```

### Rutas Públicas (sin token)

| Ruta | Método |
|---|---|
| `/api/v1/auth/login` | POST |
| `/api/v1/auth/health` | GET |

### RBAC por Ruta

| Ruta | Roles Permitidos |
|---|---|
| `/api/v1/admin/**` | ADMIN |
| `/api/v1/asignacion-docente/**` | ADMIN |
| `/api/v1/cursos/**` | ADMIN, DOCENTE |
| `/api/v1/asignaturas/**` | ADMIN, DOCENTE |
| `/api/v1/matriculas/**` | ADMIN, DOCENTE |
| `/api/v1/calificaciones/**` | ADMIN, DOCENTE |
| `/api/bff/boletin/{uuid}` | ADMIN, DOCENTE, APODERADO, ESTUDIANTE* |
| `/api/bff/dashboard/**` | ADMIN |

> \* ESTUDIANTE solo puede ver su propio boletín — se valida `userId` del token contra el UUID de la ruta.

### JWT Claims

```json
{
  "sub": "99888777-6",
  "userId": "uuid-del-usuario",
  "email": "admin@test.cl",
  "nombre": "Admin Backup",
  "role": "ADMIN",
  "rol": "ADMIN",
  "recursos": ["notas", "asistencias", "estudiantes", ...],
  "soloLectura": false,
  "iss": "ms-usuario",
  "iat": 1714298400,
  "exp": 1714384800
}
```

---

## Circuit Breaker — Resilience4j

### Parámetros por microservicio

| Parámetro | Valor |
|---|---|
| `sliding-window-size` | 10 |
| `failure-rate-threshold` | 50% |
| `wait-duration-in-open-state` | 10s |
| `timeout-duration` | 8s |

### Estados del Circuit Breaker

```
CLOSED ──[50% fallos]──► OPEN ──[10s]──► HALF-OPEN
  ▲                                         │
  └────────[3 llamadas OK]──────────────────┘
                           │
                    [sigue fallando]
                           │
                           ▼
                         OPEN
```

### Fallback Response (HTTP 503 → RFC 7807)

```json
{
  "type": "about:blank",
  "title": "Servicio no disponible",
  "status": 503,
  "detail": "El servicio no está disponible. Intente nuevamente.",
  "service": "ms-academico",
  "timestamp": "2024-05-15T12:00:00Z"
}
```

---

## Perfiles de Ejecución

| Perfil | Seguridad JWT | Eureka | Uso |
|---|---|---|---|
| `dev` | Desactivada | Deshabilitado | Desarrollo local |
| `local` | Desactivada | Deshabilitado | Rutas directas configurables |
| `docker` | Activada (RBAC) | Deshabilitado | Docker Compose |
| `prod` | Activada (RBAC) | Habilitado | Producción |

---

## Variables de Entorno

| Variable | Descripción | Default |
|---|---|---|
| `JWT_SECRET` | Clave HMAC-SHA256 (mín. 32 chars) | Requerido |
| `FRONTEND_URL` | URL del frontend para CORS | `http://localhost:3000` |
| `SPRING_PROFILES_ACTIVE` | Perfil (`dev`, `local`, `docker`, `prod`) | `prod` |

---

## Ejecución Local

### Pre-requisitos

- Java 17+
- Maven 3.9+
- Microservicios downstream corriendo

### Perfil dev (sin seguridad)

```bash
cd backend/api-gateway
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

### Perfil prod (JWT + RBAC activo)

```bash
JWT_SECRET=clave-secreta-de-32-caracteres-minimo \
SPRING_PROFILES_ACTIVE=prod \
mvn spring-boot:run
```

### Tests

```bash
mvn test
```

---

## Docker

### Construir imagen

```bash
docker build -t api-gateway:1.0.0 .
```

### Ejecutar contenedor

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e JWT_SECRET=clave-secreta-de-32-caracteres-minimo \
  api-gateway:1.0.0
```

### Con Docker Compose

```bash
docker compose -f docker-compose.test.yml up --build
```

---

*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*
*Docente: Alexis Jacob Jiménez Parada — Desarrollo Fullstack III*