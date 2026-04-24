# 🌐 API Gateway — Colegio Bernardo O'Higgins

> Punto de entrada único a la plataforma de microservicios | Proyecto Fullstack III — Duoc UC

## Índice

- [¿Qué hace el Gateway?](#qué-hace-el-gateway)
- [Diagrama de Secuencia](#diagrama-de-secuencia)
- [Rutas Configuradas](#rutas-configuradas)
- [Seguridad JWT por Perfil](#seguridad-jwt-por-perfil)
- [Circuit Breaker — Resilience4j](#circuit-breaker--resilience4j)
- [Variables de Entorno](#variables-de-entorno)
- [Levantar en perfil dev vs prod](#levantar-en-perfil-dev-vs-prod)
- [Docker](#docker)

---

## ¿Qué hace el Gateway?

El API Gateway actúa como **fachada inteligente** frente a todos los microservicios. Es el único componente que expone puertos al mundo exterior:

```
Internet / Frontend
        │
        ▼ :8080
  ┌─────────────────────────────────────────────┐
  │              API GATEWAY                     │
  │                                              │
  │  1. JwtValidationFilter   ← verifica token   │
  │  2. CircuitBreaker        ← protege el MS    │
  │  3. Route + RewritePath   ← enruta al MS     │
  │  4. FallbackController    ← responde si cae  │
  └──────────────┬──────────────────────────────┘
                 │  lb://MS-ACADEMICO (via Eureka)
                 ▼
         MS-Académico :8082
```

### Responsabilidades

| Responsabilidad | Componente |
|---|---|
| Autenticación JWT | `JwtValidationFilter` |
| Enrutamiento dinámico | Spring Cloud Gateway + Eureka |
| Tolerancia a fallos | Resilience4j Circuit Breaker |
| Degradación controlada | `FallbackController` |
| CORS global | `CorsConfig` |
| Propagación de identidad | Headers `X-User-Id`, `X-User-Role` |

---

## Diagrama de Secuencia

```
Cliente              API Gateway          Circuit Breaker        MS-Académico
   │                     │                      │                      │
   │── GET /api/v1/       │                      │                      │
   │   gestion/notas/1 ──►│                      │                      │
   │                     │                      │                      │
   │                     │◄── JwtValidationFilter ──                   │
   │                     │    (valida Bearer token)                    │
   │                     │                      │                      │
   │                [token OK]                  │                      │
   │                     │                      │                      │
   │                     │── CircuitBreaker ────►│                      │
   │                     │   academicoCB         │                      │
   │                     │                      │                      │
   │                     │              [CLOSED - normal]              │
   │                     │                      │──── lb:// ──────────►│
   │                     │                      │   (Eureka discovery)  │
   │                     │                      │                      │
   │                     │                      │◄── HTTP 200 ─────────│
   │◄── HTTP 200 ────────│                      │                      │
   │                     │                      │                      │
   ═══════════════ ESCENARIO: MS caído ════════════════════════════════
   │                     │                      │                      │
   │── GET /api/v1/ ─────►│                      │                      │
   │   gestion/notas/1    │                      │                      │
   │                     │── CircuitBreaker ────►│                      │
   │                     │   academicoCB         │                      │
   │                     │                [OPEN - 50% fallos]          │
   │                     │◄── Circuit abierto ───│                      │
   │                     │                                             │
   │                     │── forward: /fallback/academico              │
   │                     │         │                                   │
   │                     │         ▼                                   │
   │                     │   FallbackController                        │
   │                     │   HTTP 503 + Problem Details                │
   │◄── HTTP 503 ────────│                                             │
   │   (JSON amigable)   │                                             │
```

---

## Rutas Configuradas

| Ruta Pública | Destino | Método |
|---|---|---|
| `/api/v1/gestion/**` | `lb://MS-ACADEMICO` | Todos (via rewrite) |
| `/api/v1/estudiantes/**` | `lb://MS-ACADEMICO` | Todos (directo) |
| `/api/v1/notas/**` | `lb://MS-ACADEMICO` | Todos (directo) |
| `/api/v1/asistencias/**` | `lb://MS-ACADEMICO` | Todos (directo) |
| `/api/v1/reportes/**` | `lb://MS-ACADEMICO` | Todos (directo) |
| `/fallback/**` | `FallbackController` | GET |
| `/actuator/health` | Gateway local | GET |

### Rewrite de rutas

```
Request externo:  GET /api/v1/gestion/estudiantes/1
                              ↓  RewritePath
Request al MS:    GET /api/v1/estudiantes/1
```

---

## Seguridad JWT por Perfil

### Perfil `dev` — Desarrollo local

```yaml
# Sin validación JWT — ideal para pruebas con Postman/Swagger
spring:
  profiles:
    active: dev
```

- ✅ Todas las rutas accesibles sin token
- ✅ Log en DEBUG para ver el routing
- ⚠️ Aviso en consola: **"Seguridad JWT DESACTIVADA"**

### Perfil `prod` — Producción

```yaml
spring:
  profiles:
    active: prod
```

- 🔒 Todas las rutas requieren header `Authorization: Bearer <token>`
- 🔒 Token validado con HMAC-SHA256
- 🔒 Headers `X-User-Id` y `X-User-Role` propagados al MS downstream
- ❌ Sin token → HTTP 401 (RFC 7807 Problem Details)
- ❌ Token expirado → HTTP 401 con mensaje específico

### Ejemplo de request con token (prod)

```bash
curl -X GET http://localhost:8080/api/v1/gestion/estudiantes \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Respuesta 401 (RFC 7807)

```json
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Token de autenticación requerido",
  "timestamp": "2024-05-15T12:00:00Z"
}
```

---

## Circuit Breaker — Resilience4j

### Parámetros `academicoCB`

| Parámetro | Valor | Descripción |
|---|---|---|
| `sliding-window-size` | `10` | Evalúa las últimas 10 llamadas |
| `failure-rate-threshold` | `50%` | Abre el circuito si 5 de 10 fallan |
| `wait-duration-in-open-state` | `10s` | Espera 10s antes de intentar recuperar |
| `permitted-number-of-calls-in-half-open-state` | `3` | 3 llamadas de prueba en HALF-OPEN |
| `slow-call-duration-threshold` | `5s` | Llamadas > 5s se consideran lentas |
| `slow-call-rate-threshold` | `80%` | Abre si 80% de las llamadas son lentas |
| `timeout-duration` | `8s` | Timeout total por llamada |

### Estados del Circuit Breaker

```
CLOSED ──[50% fallas]──► OPEN ──[10s]──► HALF-OPEN
  ▲                                           │
  └────────[3 llamadas OK]────────────────────┘
                            │
                     [sigue fallando]
                            │
                            ▼
                          OPEN
```

### Fallback Response (HTTP 503)

```json
{
  "type": "about:blank",
  "title": "Servicio no disponible",
  "status": 503,
  "detail": "El servicio de Gestión Académica no está disponible en este momento. Por favor, intente nuevamente en unos minutos.",
  "service": "ms-academico",
  "timestamp": "2024-05-15T12:00:00Z"
}
```

---

## Variables de Entorno

| Variable | Descripción | Default |
|---|---|---|
| `JWT_SECRET` | Clave secreta HMAC para validar tokens | `colegio-bernardo-...` (inseguro, solo dev) |
| `EUREKA_URL` | URL del servidor de discovery | `http://localhost:8761/eureka` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo (`dev` o `prod`) | `prod` en Docker |

---

## Levantar en perfil dev vs prod

### Desarrollo local (sin token)

```bash
cd backend/api-gateway

# Con Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Con variable de entorno
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

### Producción (validación JWT activa)

```bash
# Con Maven
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Variables de entorno obligatorias en prod:
JWT_SECRET=tu-clave-secreta-muy-larga \
EUREKA_URL=http://eureka-server:8761/eureka \
SPRING_PROFILES_ACTIVE=prod \
mvn spring-boot:run
```

---

## Docker

### Construir imagen

```bash
docker build -t api-gateway:1.0.0 .
```

### Ejecutar en prod

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JWT_SECRET=tu-clave-secreta-segura-de-al-menos-32-chars \
  -e EUREKA_URL=http://eureka-server:8761/eureka \
  api-gateway:1.0.0
```

### Ejecutar en dev (sin JWT)

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  api-gateway:1.0.0
```

### Con Docker Compose (ecosistema completo)

```bash
# Desde la raíz del proyecto
docker-compose up api-gateway ms-academico
```

---

*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*
*Docente: Alexis Jacob Jiménez Parada — Desarrollo Fullstack III*
