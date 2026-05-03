# 🌐 API Gateway — Colegio Bernardo O'Higgins

> Punto de entrada único a la plataforma de microservicios | Proyecto Fullstack III — Duoc UC

## Índice

- [¿Qué hace el Gateway?](#qué-hace-el-gateway)
- [Diagrama de Secuencia](#diagrama-de-secuencia)
- [Rutas Configuradas](#rutas-configuradas)
- [Circuit Breaker — Resilience4j](#circuit-breaker--resilience4j)
- [Seguridad JWT por Perfil](#seguridad-jwt-por-perfil)
- [Variables de Entorno](#variables-de-entorno)
- [Ejecución Local](#ejecución-local)
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
  └──────┬──────────────┬──────────────┬─────────┘
         │              │              │
         ▼ lb://        ▼ lb://        ▼ lb://
   MS-Usuario     MS-Académico    MS-Comunicaciones
     :8083           :8082            :8085
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

### Flujo de autenticación (nuevo)

```
Cliente             API Gateway         MS-Usuario
   │                    │                    │
   │─ POST /api/v1/auth/login ──────────────►│
   │                    │                    │
   │                    │  [CircuitBreaker   │
   │                    │   usuarioCB]       │
   │                    │                   [verifica email/password]
   │                    │                   [genera JWT con claims de rol]
   │                    │◄──── 200 + JWT ────│
   │◄── 200 + JWT ──────│                    │
```

### Flujo de recurso protegido

```
Cliente             API Gateway         MS-Académico
   │                    │                    │
   │─ GET /api/v1/gestion/notas/1 ──────────►│
   │   Authorization: Bearer <jwt>           │
   │                    │                    │
   │            [JwtValidationFilter]        │
   │            [valida HMAC-SHA256]         │
   │            [extrae rol del claim]       │
   │                    │                    │
   │                [token OK]               │
   │                    │── CircuitBreaker ──►│
   │                    │   academicoCB       │
   │                    │                    │─ consulta BD
   │                    │◄── HTTP 200 ────────│
   │◄── HTTP 200 ────────│                   │

═══════════════ ESCENARIO: MS caído ═══════════════

   │─ GET /api/v1/gestion/notas/1 ──────────►│
   │                    │── CB abierto ──────►│ (sin respuesta)
   │                    │◄── forward: /fallback/academico
   │◄── HTTP 503 ────────│
   │   (JSON RFC 7807)  │
```

---

## Rutas Configuradas

| Ruta Pública | Destino | Circuit Breaker | Notas |
|---|---|---|---|
| `POST /api/v1/auth/login` | `lb://MS-USUARIO` | `usuarioCB` | Pública — sin JWT |
| `POST /api/v1/auth/register` | `lb://MS-USUARIO` | `usuarioCB` | Pública — sin JWT |
| `GET /api/v1/auth/health` | `lb://MS-USUARIO` | `usuarioCB` | Pública |
| `/api/v1/gestion/**` | `lb://MS-ACADEMICO` | `academicoCB` | Con rewrite de ruta |
| `/api/v1/estudiantes/**` | `lb://MS-ACADEMICO` | `academicoCB` | Ruta directa |
| `/api/v1/notas/**` | `lb://MS-ACADEMICO` | `academicoCB` | Ruta directa |
| `/api/v1/asistencias/**` | `lb://MS-ACADEMICO` | `academicoCB` | Ruta directa |
| `/api/v1/reportes/**` | `lb://MS-ACADEMICO` | `academicoCB` | Ruta directa |
| `/api/v1/comunicaciones/**` | `lb://MS-COMUNICACIONES` | `comunicacionesCB` | Con rewrite de ruta |
| `/fallback/**` | `FallbackController` | — | Respuesta de degradación |
| `/actuator/health` | Gateway local | — | Health check público |

### Rewrite de rutas

```
GET /api/v1/gestion/estudiantes/1     → GET /api/v1/estudiantes/1     (MS-Académico)
GET /api/v1/comunicaciones/notificaciones → GET /api/notificaciones   (MS-Comunicaciones)
```

---

## Circuit Breaker — Resilience4j

### Instancias configuradas

| Instancia | Protege | `failure-rate` | `wait-open` |
|---|---|---|---|
| `academicoCB` | MS-Académico | 50% | 10s |
| `usuarioCB` | MS-Usuario | 50% | 10s |
| `comunicacionesCB` | MS-Comunicaciones | 50% | 10s |
| `defaultCB` | Fallback genérico | 50% | 15s |

### Parámetros detallados (`academicoCB` / `usuarioCB`)

| Parámetro | Valor | Descripción |
|---|---|---|
| `sliding-window-size` | `10` | Evalúa las últimas 10 llamadas |
| `failure-rate-threshold` | `50%` | Abre si 5 de 10 fallan |
| `wait-duration-in-open-state` | `10s` | Espera antes de intentar HALF-OPEN |
| `permitted-number-of-calls-in-half-open-state` | `3` | Llamadas de prueba en HALF-OPEN |
| `slow-call-duration-threshold` | `5s` | Llamadas > 5s se consideran lentas |
| `slow-call-rate-threshold` | `80%` | Abre si 80% son lentas |
| `timeout-duration` | `8s` | Timeout total por llamada |

### Estados del Circuit Breaker

```
CLOSED ──[50% fallas]──► OPEN ──[10s]──► HALF-OPEN
  ▲                                           │
  └────────[3 llamadas OK]────────────────────┘
                            │
                     [sigue fallando]
                            ▼
                          OPEN
```

### Respuesta fallback (HTTP 503 — RFC 7807)

```json
{
  "type": "about:blank",
  "title": "Servicio no disponible",
  "status": 503,
  "detail": "El servicio de Gestión Académica no está disponible en este momento.",
  "service": "ms-academico",
  "timestamp": "2024-05-15T12:00:00Z"
}
```

---

## Seguridad JWT por Perfil

### Perfil `dev` — Desarrollo local

- ✅ Todas las rutas accesibles sin token
- ✅ Log en DEBUG para ver el routing
- ⚠️ Aviso en consola: **"Seguridad JWT DESACTIVADA"**

### Perfil `prod` — Producción

- 🔒 Todas las rutas (excepto `/api/v1/auth/**` y `/actuator/health`) requieren `Authorization: Bearer <token>`
- 🔒 Token validado con HMAC-SHA256 usando el mismo `JWT_SECRET` que usa MS-Usuario
- 🔒 Headers `X-User-Id` y `X-User-Role` propagados downstream
- ❌ Sin token → HTTP 401 (RFC 7807)
- ❌ Token expirado → HTTP 401 con mensaje específico

### Ejemplo de request autenticado

```bash
# 1. Login para obtener el token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"docente@colegio.cl","password":"password123"}'

# 2. Usar el token en requests protegidos
curl http://localhost:8080/api/v1/gestion/notas/estudiante/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## Variables de Entorno

| Variable | Descripción | Default |
|---|---|---|
| `JWT_SECRET` | Clave secreta HMAC para validar tokens (mín. 32 chars) | `colegio-bernardo-...` (solo dev) |
| `EUREKA_URL` | URL del servidor de discovery | `http://localhost:8761/eureka` |
| `ACADEMICO_URI` | URI del MS-Académico (perfil local) | `http://localhost:8082` |
| `USUARIO_URI` | URI del MS-Usuario (perfil local) | `http://localhost:8083` |
| `COMUNICACIONES_URI` | URI del MS-Comunicaciones (perfil local) | `http://localhost:8085` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo (`dev` o `prod`) | `prod` en Docker |

---

## Ejecución Local

### Perfil `dev,local` (sin Eureka, sin JWT — recomendado para desarrollo)

```bash
cd backend/api-gateway
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

El perfil `local` usa el archivo `application-local.yml` que apunta directamente a `localhost` sin necesitar Eureka.

### Perfil `prod` (validación JWT activa)

```bash
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

### Ejecutar en dev

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev,local \
  -e ACADEMICO_URI=http://host.docker.internal:8082 \
  -e USUARIO_URI=http://host.docker.internal:8083 \
  api-gateway:1.0.0
```

### Con Docker Compose (ecosistema completo)

```bash
# Desde la raíz del proyecto
docker compose up --build

# Solo gateway + MSs backend
docker compose up postgres postgres-usuario ms-academico ms-usuario api-gateway
```

---

*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*  
*Docente: Alexis Jacob Jiménez Parada — Desarrollo Fullstack III*
