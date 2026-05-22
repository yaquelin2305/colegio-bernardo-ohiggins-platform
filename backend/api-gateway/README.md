# API Gateway — Colegio Bernardo O'Higgins

> Punto de entrada único a la plataforma de microservicios | Proyecto Fullstack III — Duoc UC

## Índice

- [¿Qué hace el Gateway?](#qué-hace-el-gateway)
- [Rutas Configuradas](#rutas-configuradas)
- [Seguridad JWT + RBAC](#seguridad-jwt--rbac)
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
  │  1. Spring Security   ← valida JWT + RBAC    │
  │  2. JwtValidationFilter ← self-access check  │
  │  3. CORS centralizado  ← headers globales    │
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
| Autenticación JWT | `JwtReactiveAuthenticationManager` |
| Autorización RBAC | `SecurityConfigProd` (ADMIN, DOCENTE, APODERADO, ESTUDIANTE) |
| Self-access check | `JwtValidationFilter` (estudiante ve solo su boletín) |
| CORS global | `application.yml` globalcors |
| Propagación de identidad | Headers `X-User-Id`, `X-User-Role`, `X-User-Uuid` |

---

## Rutas Configuradas

### Perfil `dev` (localhost, sin Eureka)

| Ruta | Destino |
|---|---|
| `/api/v1/auth/**` | `localhost:8083` |
| `/api/v1/admin/**` | `localhost:8083` |
| `/api/v1/usuarios/**` | `localhost:8083` |
| `/api/v1/cursos/**` | `localhost:8082` |
| `/api/v1/asignaturas/**` | `localhost:8082` |
| `/api/v1/matriculas/**` | `localhost:8082` |
| `/api/v1/calificaciones/**` | `localhost:8082` |
| `/api/v1/asignacion-docente/**` | `localhost:8082` |
| `/api/v1/estudiantes/**` | `localhost:8082` |
| `/api/v1/notas/**` | `localhost:8082` |
| `/api/v1/asistencias/**` | `localhost:8082` |
| `/api/v1/reportes/**` | `localhost:8082` |
| `/api/v1/comunicaciones/**` | `localhost:8085` |
| `/api/asistencia/**` | `localhost:8085` |
| `/api/bff/**` | `localhost:8084` |

### Perfil `prod` (Docker Compose, URIs de contenedor)

| Ruta | Destino |
|---|---|
| `/api/v1/auth/**` | `ms-usuario:8083` |
| `/api/v1/admin/**` | `ms-usuario:8083` |
| `/api/v1/usuarios/**` | `ms-usuario:8083` |
| `/api/v1/cursos/**` | `ms-academico:8082` |
| `/api/v1/asignaturas/**` | `ms-academico:8082` |
| `/api/v1/matriculas/**` | `ms-academico:8082` |
| `/api/v1/calificaciones/**` | `ms-academico:8082` |
| `/api/v1/asignacion-docente/**` | `ms-academico:8082` |
| `/api/v1/notas/**` | `ms-academico:8082` |
| `/api/v1/asistencias/**` | `ms-academico:8082` |
| `/api/v1/estudiantes/**` | `ms-academico:8082` |
| `/api/v1/reportes/**` | `ms-academico:8082` |
| `/api/v1/comunicaciones/**` | `ms-comunicaciones:8081` |
| `/api/asistencia/**` | `ms-asistencia:8082` |
| `/api/bff/**` | `ms-bff:8084` |

---

## Seguridad JWT + RBAC

### Flujo de autenticación

```
1. POST /api/v1/auth/login  → bypass (público)
2. MS-Usuario valida RUT + BCrypt → genera JWT
3. Frontend almacena JWT en localStorage
4. Todas las demás peticiones incluyen: Authorization: Bearer <token>
5. JwtReactiveAuthenticationManager valida firma HMAC-SHA256 y expiración
6. SecurityConfigProd: RBAC por ruta
7. JwtValidationFilter: self-access check para estudiantes
```

### Seguridad en 2 capas

| Capa | Componente | Qué hace |
|---|---|---|
| Gruesa | `SecurityConfigProd` (Spring Security) | Valida JWT, asigna `ROLE_*`, verifica RBAC por ruta |
| Fina | `JwtValidationFilter` (GlobalFilter) | Estudiante solo ve su propio boletín, propaga `X-User-*` headers |

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
  "recursos": ["notas", "asistencias", "estudiantes", "..."],
  "soloLectura": false,
  "iss": "ms-usuario",
  "iat": 1714298400,
  "exp": 1714384800
}
```

---

## Perfiles de Ejecución

| Perfil | Seguridad JWT | Eureka | Uso |
|---|---|---|---|
| `dev` | Desactivada | Deshabilitado | Desarrollo local |
| `prod` | Activada (RBAC) | Deshabilitado | Docker Compose |

---

## Variables de Entorno

| Variable | Descripción | Default |
|---|---|---|
| `JWT_SECRET` | Clave HMAC-SHA256 (mín. 32 chars) | Valor por defecto en YAML |
| `FRONTEND_URL` | URL del frontend para CORS | `http://localhost:5173` |
| `SPRING_PROFILES_ACTIVE` | Perfil (`dev`, `prod`) | `prod` |
| `ACADEMICO_URI` | URI de ms-academico (solo perfil `dev`) | `http://localhost:8082` |
| `USUARIO_URI` | URI de ms-usuario (solo perfil `dev`) | `http://localhost:8083` |
| `COMUNICACIONES_URI` | URI de ms-comunicaciones (solo perfil `dev`) | `http://localhost:8085` |
| `ASISTENCIA_URI` | URI de ms-asistencia (solo perfil `dev`) | `http://localhost:8085` |
| `BFF_URI` | URI de ms-bff (solo perfil `dev`) | `http://localhost:8084` |

---

## Ejecución Local

### Pre-requisitos

- Java 17+
- Maven 3.9+
- Microservicios downstream corriendo

### Perfil dev (sin seguridad, rutas a localhost)

```bash
cd backend/api-gateway
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Perfil prod (JWT + RBAC activo, rutas a contenedores Docker)

```bash
JWT_SECRET=clave-secreta-de-32-caracteres-minimo \
SPRING_PROFILES_ACTIVE=prod \
mvn spring-boot:run
```

---

## Docker

### Con Docker Compose

```bash
docker compose up --build
```

El docker-compose ya configura `SPRING_PROFILES_ACTIVE=prod` automáticamente.

---

*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*
*Docente: Alexis Jacob Jiménez Parada — Desarrollo Fullstack III*
