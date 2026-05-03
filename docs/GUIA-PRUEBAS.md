# 🧪 Guía de Pruebas — Plataforma Colegio Bernardo O'Higgins

> Guía completa para levantar, probar y verificar el **MS-Académico** y el **API Gateway**.
> Incluye también la integración con **MS-Comunicaciones**.

---

## Índice

1. [Requisitos previos](#1-requisitos-previos)
2. [Orden de arranque](#2-orden-de-arranque)
3. [Levantar MS-Académico](#3-levantar-ms-académico)
4. [Levantar API Gateway](#4-levantar-api-gateway)
5. [Levantar MS-Comunicaciones](#5-levantar-ms-comunicaciones)
6. [Verificar que todo está vivo](#6-verificar-que-todo-está-vivo)
7. [CURLs — MS-Académico directo (sin Gateway)](#7-curls--ms-académico-directo-sin-gateway)
8. [CURLs — MS-Académico vía API Gateway](#8-curls--ms-académico-vía-api-gateway)
9. [CURLs — MS-Comunicaciones vía API Gateway](#9-curls--ms-comunicaciones-vía-api-gateway)
10. [Swagger UI](#10-swagger-ui)
11. [Flujo de prueba completo de punta a punta](#11-flujo-de-prueba-completo-de-punta-a-punta)
12. [Errores frecuentes y soluciones](#12-errores-frecuentes-y-soluciones)

---

## 1. Requisitos previos

| Herramienta | Versión mínima | Verificar con |
|---|---|---|
| Java | 17 | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| PostgreSQL | 14+ (o Supabase) | `psql --version` |
| cURL | Cualquier | `curl --version` |

### Base de datos — crear schemas en PostgreSQL

Ejecutá esto en tu PostgreSQL local o en Supabase:

```sql
-- Para MS-Académico
CREATE DATABASE academico_db;

-- Para MS-Comunicaciones
CREATE DATABASE notificaciones_db;
```

> **Supabase**: usá la URL de conexión que te da el dashboard.
> Formato: `jdbc:postgresql://db.<ref>.supabase.co:5432/postgres`

---

## 2. Orden de arranque

> ⚠️ El orden importa. El Gateway necesita que los MSs estén registrados en Eureka.
> Si no tenés Eureka corriendo, arrancá los MSs con `spring.cloud.discovery.enabled=false`.

```
Orden recomendado:
  1. PostgreSQL (ya debe estar corriendo)
  2. MS-Académico    → puerto 8082
  3. MS-Comunicaciones → puerto 8085
  4. API Gateway     → puerto 8080
```

---

## 3. Levantar MS-Académico

```bash
cd "C:\Duoc\Proyecto FS3\backend\ms-academico"
```

### Opción A — Sin Eureka (más simple para pruebas locales)

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.cloud.discovery.enabled=false" ^
  -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:postgresql://localhost:5432/academico_db --spring.datasource.username=postgres --spring.datasource.password=TU_PASSWORD --spring.jpa.hibernate.ddl-auto=create-drop"
```

### Opción B — Variables de entorno (más limpio)

```bash
set DB_URL=jdbc:postgresql://localhost:5432/academico_db
set DB_USERNAME=postgres
set DB_PASSWORD=TU_PASSWORD
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.cloud.discovery.enabled=false"
```

### Opción C — Supabase

```bash
set DB_URL=jdbc:postgresql://db.XXXX.supabase.co:5432/postgres
set DB_USERNAME=postgres
set DB_PASSWORD=TU_PASSWORD_SUPABASE
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.cloud.discovery.enabled=false -Dspring.jpa.hibernate.ddl-auto=update"
```

### ✅ Verificar que levantó

```
Buscá en la consola:
  Started MsAcademicoApplication in X.XXX seconds
  Tomcat started on port(s): 8082
```

---

## 4. Levantar API Gateway

```bash
cd "C:\Duoc\Proyecto FS3\backend\api-gateway"
```

### Perfil DEV (sin validación JWT — recomendado para pruebas)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev ^
  -Dspring-boot.run.jvmArguments="-Dspring.cloud.discovery.enabled=false" ^
  -Dspring-boot.run.arguments="--spring.cloud.gateway.routes[0].uri=http://localhost:8082 --spring.cloud.gateway.routes[1].uri=http://localhost:8082"
```

### Forma más simple — deshabilitar Eureka y apuntar directo al MS

Creá un archivo temporal `application-local.yml` en `src/main/resources/`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: academico-route
          uri: http://localhost:8082
          predicates:
            - Path=/api/v1/gestion/**
          filters:
            - RewritePath=/api/v1/gestion/(?<segment>.*), /api/v1/${segment}
        - id: academico-direct-route
          uri: http://localhost:8082
          predicates:
            - Path=/api/v1/estudiantes/**, /api/v1/notas/**, /api/v1/asistencias/**, /api/v1/reportes/**
        - id: comunicaciones-route
          uri: http://localhost:8085
          predicates:
            - Path=/api/v1/comunicaciones/**
          filters:
            - RewritePath=/api/v1/comunicaciones/(?<segment>.*), /api/${segment}
    discovery:
      enabled: false
  profiles:
    active: dev
eureka:
  client:
    enabled: false
```

Luego levantá con:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

### ✅ Verificar que levantó

```
Buscá en la consola:
  Started ApiGatewayApplication in X.XXX seconds
  Netty started on port 8080
  [DEV] Seguridad JWT desactivada
```

---

## 5. Levantar MS-Comunicaciones

```bash
cd "C:\Duoc\Proyecto FS3\backend\ms-comunicaciones"
```

```bash
set DB_PASSWORD=TU_PASSWORD
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### ✅ Verificar que levantó

```
Buscá en la consola:
  Started NotificacionApplication in X.XXX seconds
  Tomcat started on port(s): 8085
```

---

## 6. Verificar que todo está vivo

Ejecutá estos tres CURLs antes de cualquier prueba:

```bash
# MS-Académico — health directo
curl -s http://localhost:8082/actuator/health | findstr "status"

# API Gateway — health
curl -s http://localhost:8080/actuator/health | findstr "status"

# MS-Comunicaciones — health directo
curl -s http://localhost:8085/actuator/health | findstr "status"
```

Respuesta esperada en cada uno:
```json
{"status":"UP"}
```

---

## 7. CURLs — MS-Académico directo (sin Gateway)

> Puerto: **8082** — Útil para probar el MS aislado

---

### 👤 ESTUDIANTES

#### Crear estudiante

```bash
curl -s -X POST http://localhost:8082/api/v1/estudiantes ^
  -H "Content-Type: application/json" ^
  -d "{\"rut\":\"12345678-9\",\"nombre\":\"Juan\",\"apellido\":\"Perez\",\"curso\":8}" ^
  | python -m json.tool
```

**Body de ejemplo:**
```json
{
  "rut": "12345678-9",
  "nombre": "Juan",
  "apellido": "Perez",
  "curso": 8
}
```

**Respuesta esperada (201 Created):**
```json
{
  "id": 1,
  "rut": "12345678-9",
  "nombre": "Juan",
  "apellido": "Perez",
  "curso": 8,
  "nombreCompleto": "Juan Perez"
}
```

---

#### Obtener todos los estudiantes

```bash
curl -s http://localhost:8082/api/v1/estudiantes | python -m json.tool
```

---

#### Obtener estudiante por ID

```bash
curl -s http://localhost:8082/api/v1/estudiantes/1 | python -m json.tool
```

---

#### Obtener estudiante por RUT

```bash
curl -s http://localhost:8082/api/v1/estudiantes/rut/12345678-9 | python -m json.tool
```

---

#### Listar estudiantes por curso

```bash
curl -s http://localhost:8082/api/v1/estudiantes/curso/8 | python -m json.tool
```

---

#### Actualizar estudiante

```bash
curl -s -X PUT http://localhost:8082/api/v1/estudiantes/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"rut\":\"12345678-9\",\"nombre\":\"Juan Carlos\",\"apellido\":\"Perez\",\"curso\":8}" ^
  | python -m json.tool
```

---

#### Eliminar estudiante

```bash
curl -s -X DELETE http://localhost:8082/api/v1/estudiantes/1 -o nul -w "HTTP Status: %{http_code}\n"
```

**Respuesta esperada:** `HTTP Status: 204`

---

### 📚 NOTAS

#### Registrar nota

```bash
curl -s -X POST http://localhost:8082/api/v1/notas ^
  -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Matematicas\",\"nota\":5.5,\"tipo\":\"PRUEBA\",\"fecha\":\"2024-05-15\",\"descripcion\":\"Prueba unidad 1\"}" ^
  | python -m json.tool
```

**Body de ejemplo:**
```json
{
  "studentId": 1,
  "asignatura": "Matematicas",
  "nota": 5.5,
  "tipo": "PRUEBA",
  "fecha": "2024-05-15",
  "descripcion": "Prueba unidad 1"
}
```

> Tipos válidos: `PRUEBA`, `TAREA`, `EXAMEN`, `TRABAJO`
> Nota válida: entre `1.0` y `7.0`

---

#### Listar notas de un estudiante

```bash
curl -s http://localhost:8082/api/v1/notas/estudiante/1 | python -m json.tool
```

---

#### Listar notas por asignatura

```bash
curl -s "http://localhost:8082/api/v1/notas/estudiante/1/asignatura/Matematicas" | python -m json.tool
```

---

#### Calcular promedio general

```bash
curl -s http://localhost:8082/api/v1/notas/estudiante/1/promedio | python -m json.tool
```

**Respuesta esperada:**
```json
{
  "promedio": 5.5
}
```

---

#### Calcular promedio por asignatura

```bash
curl -s "http://localhost:8082/api/v1/notas/estudiante/1/promedio/asignatura/Matematicas" | python -m json.tool
```

---

#### Actualizar nota

```bash
curl -s -X PUT http://localhost:8082/api/v1/notas/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Matematicas\",\"nota\":6.0,\"tipo\":\"PRUEBA\",\"fecha\":\"2024-05-15\",\"descripcion\":\"Corregida\"}" ^
  | python -m json.tool
```

---

#### Eliminar nota

```bash
curl -s -X DELETE http://localhost:8082/api/v1/notas/1 -o nul -w "HTTP Status: %{http_code}\n"
```

---

### 📝 ASISTENCIAS

#### Registrar asistencia (presente)

```bash
curl -s -X POST http://localhost:8082/api/v1/asistencias ^
  -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Matematicas\",\"fecha\":\"2024-05-15\",\"presente\":true}" ^
  | python -m json.tool
```

---

#### Registrar asistencia (ausente con justificación)

```bash
curl -s -X POST http://localhost:8082/api/v1/asistencias ^
  -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Historia\",\"fecha\":\"2024-05-16\",\"presente\":false,\"justificacion\":\"Certificado medico\"}" ^
  | python -m json.tool
```

---

#### Listar asistencias de un estudiante

```bash
curl -s http://localhost:8082/api/v1/asistencias/estudiante/1 | python -m json.tool
```

---

#### Consultar porcentaje de asistencia y riesgo de repitencia

```bash
curl -s http://localhost:8082/api/v1/asistencias/estudiante/1/porcentaje | python -m json.tool
```

**Respuesta esperada:**
```json
{
  "studentId": 1,
  "porcentajeAsistencia": 75.0,
  "enRiesgoRepitencia": true
}
```

> ⚠️ `enRiesgoRepitencia: true` cuando asistencia < 85%

---

### 📊 REPORTES

#### Generar reporte académico completo

```bash
curl -s http://localhost:8082/api/v1/reportes/estudiante/1 | python -m json.tool
```

**Respuesta esperada (alumno con problemas):**
```json
{
  "studentId": 1,
  "nombreEstudiante": "Juan Perez",
  "curso": "8",
  "promedio": 3.2,
  "porcentajeAsistencia": 70.0,
  "alerta": "ALERTA_CRITICA",
  "mensajeAlerta": "⚠️ ALERTA CRÍTICA: Promedio 3.2 (bajo 4.0) y asistencia 70.0% (bajo 85%). Riesgo alto de repitencia.",
  "fechaGeneracion": "2024-05-15",
  "asignaturasReprobadas": ["Matematicas", "Historia"]
}
```

**Tipos de alerta posibles:**
| Alerta | Condición |
|---|---|
| `SIN_ALERTA` | Promedio ≥ 4.0 y asistencia ≥ 85% |
| `ALERTA_RENDIMIENTO` | Promedio < 4.0 |
| `ALERTA_ASISTENCIA` | Asistencia < 85% |
| `ALERTA_CRITICA` | Ambas condiciones |

---

## 8. CURLs — MS-Académico vía API Gateway

> Puerto Gateway: **8080** — Así llegaría el frontend/BFF en producción

### Con perfil DEV (sin token)

Todos los endpoints son identicos pero en puerto `8080`.
Gateway hace el rewrite: `/api/v1/gestion/X` → `/api/v1/X` en el MS.

#### Ruta con prefijo `/gestion` (ruta canónica del Gateway)

```bash
# Listar estudiantes
curl -s http://localhost:8080/api/v1/gestion/estudiantes | python -m json.tool

# Crear estudiante
curl -s -X POST http://localhost:8080/api/v1/gestion/estudiantes ^
  -H "Content-Type: application/json" ^
  -d "{\"rut\":\"98765432-1\",\"nombre\":\"Maria\",\"apellido\":\"Gonzalez\",\"curso\":6}" ^
  | python -m json.tool

# Notas
curl -s http://localhost:8080/api/v1/gestion/notas/estudiante/1 | python -m json.tool

# Promedio
curl -s http://localhost:8080/api/v1/gestion/notas/estudiante/1/promedio | python -m json.tool

# Reporte
curl -s http://localhost:8080/api/v1/gestion/reportes/estudiante/1 | python -m json.tool
```

#### Ruta directa (sin prefijo `/gestion`)

```bash
# También funciona sin el prefijo — ruta directa
curl -s http://localhost:8080/api/v1/estudiantes | python -m json.tool
curl -s http://localhost:8080/api/v1/notas/estudiante/1 | python -m json.tool
curl -s http://localhost:8080/api/v1/reportes/estudiante/1 | python -m json.tool
```

---

### Con perfil PROD (con token JWT)

Primero necesitás un token. Si todavía no tienen MS-Users, podés generar uno de prueba:

```bash
# Generar token JWT de prueba (necesitás instalar jwt-cli o usar jwt.io)
# Alternativamente usa este token pre-generado para pruebas (expira 2099):
set TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsInJvbGUiOiJET0NFTlRFIiwiaWF0IjoxNzE2MDAwMDAwLCJleHAiOjQxMDI0NDQ4MDB9.placeholder
```

> Para pruebas reales en prod, el token lo emite el MS-Users.

```bash
# Con token en perfil prod
curl -s http://localhost:8080/api/v1/gestion/estudiantes ^
  -H "Authorization: Bearer %TOKEN%" ^
  | python -m json.tool

# Sin token → 401 Unauthorized
curl -s http://localhost:8080/api/v1/gestion/estudiantes ^
  -w "\nHTTP Status: %{http_code}\n"
```

**Respuesta sin token (401 RFC 7807):**
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

### Probar el Circuit Breaker

```bash
# 1. Detener MS-Académico (Ctrl+C en su terminal)

# 2. Hacer una petición — debería retornar el fallback 503
curl -s http://localhost:8080/api/v1/gestion/estudiantes | python -m json.tool
```

**Respuesta esperada (503 Fallback):**
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

## 9. CURLs — MS-Comunicaciones vía API Gateway

> MS-Comunicaciones corre en puerto **8085**
> Ruta en Gateway: `/api/v1/comunicaciones/**` → reescribe a `/api/**` en el MS

### Directo al MS (puerto 8085)

#### Enviar notificación

```bash
curl -s -X POST http://localhost:8085/api/notificaciones ^
  -H "Content-Type: application/json" ^
  -d "{\"emisor\":\"Docente Juan\",\"destinatario\":\"Apoderado Maria\",\"asunto\":\"Alerta rendimiento\",\"mensaje\":\"Su hijo presenta promedio bajo 4.0. Por favor contactar al colegio.\",\"fechaEnvio\":\"2024-05-15T10:00:00\"}" ^
  | python -m json.tool
```

**Body de ejemplo:**
```json
{
  "emisor": "Docente Juan",
  "destinatario": "Apoderado Maria",
  "asunto": "Alerta rendimiento",
  "mensaje": "Su hijo presenta promedio bajo 4.0. Por favor contactar al colegio.",
  "fechaEnvio": "2024-05-15T10:00:00"
}
```

#### Listar notificaciones

```bash
curl -s http://localhost:8085/api/notificaciones | python -m json.tool
```

---

### Vía API Gateway (puerto 8080)

```bash
# Enviar notificación vía Gateway
curl -s -X POST http://localhost:8080/api/v1/comunicaciones/notificaciones ^
  -H "Content-Type: application/json" ^
  -d "{\"emisor\":\"Docente Juan\",\"destinatario\":\"Apoderado Pedro\",\"asunto\":\"Inasistencia\",\"mensaje\":\"Su hijo estuvo ausente hoy.\",\"fechaEnvio\":\"2024-05-15T08:00:00\"}" ^
  | python -m json.tool

# Listar vía Gateway
curl -s http://localhost:8080/api/v1/comunicaciones/notificaciones | python -m json.tool
```

---

## 10. Swagger UI

Abrí en el navegador:

| Servicio | URL Swagger |
|---|---|
| MS-Académico | http://localhost:8082/swagger-ui.html |
| MS-Académico (API docs JSON) | http://localhost:8082/v3/api-docs |

> El API Gateway no tiene Swagger propio. Los MSs individuales documentan sus endpoints.

---

## 11. Flujo de prueba completo de punta a punta

Seguí estos pasos en orden para probar el sistema completo:

### Paso 1 — Crear dos estudiantes

```bash
curl -s -X POST http://localhost:8082/api/v1/estudiantes ^
  -H "Content-Type: application/json" ^
  -d "{\"rut\":\"11111111-1\",\"nombre\":\"Ana\",\"apellido\":\"Torres\",\"curso\":7}"

curl -s -X POST http://localhost:8082/api/v1/estudiantes ^
  -H "Content-Type: application/json" ^
  -d "{\"rut\":\"22222222-2\",\"nombre\":\"Carlos\",\"apellido\":\"Mendez\",\"curso\":7}"
```

### Paso 2 — Registrar notas (alumno 1 aprueba, alumno 2 reprueba)

```bash
REM Alumno 1 — notas altas
curl -s -X POST http://localhost:8082/api/v1/notas -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Matematicas\",\"nota\":6.5,\"tipo\":\"PRUEBA\",\"fecha\":\"2024-05-10\"}"
curl -s -X POST http://localhost:8082/api/v1/notas -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Historia\",\"nota\":5.8,\"tipo\":\"EXAMEN\",\"fecha\":\"2024-05-11\"}"

REM Alumno 2 — notas bajas
curl -s -X POST http://localhost:8082/api/v1/notas -H "Content-Type: application/json" ^
  -d "{\"studentId\":2,\"asignatura\":\"Matematicas\",\"nota\":2.5,\"tipo\":\"PRUEBA\",\"fecha\":\"2024-05-10\"}"
curl -s -X POST http://localhost:8082/api/v1/notas -H "Content-Type: application/json" ^
  -d "{\"studentId\":2,\"asignatura\":\"Historia\",\"nota\":3.0,\"tipo\":\"EXAMEN\",\"fecha\":\"2024-05-11\"}"
```

### Paso 3 — Registrar asistencias (alumno 2 con baja asistencia)

```bash
REM Alumno 1 — 100% asistencia
for /L %i in (1,1,5) do curl -s -X POST http://localhost:8082/api/v1/asistencias ^
  -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Matematicas\",\"fecha\":\"2024-05-1%i\",\"presente\":true}"

REM Alumno 2 — 60% asistencia (3 de 5)
curl -s -X POST http://localhost:8082/api/v1/asistencias -H "Content-Type: application/json" ^
  -d "{\"studentId\":2,\"asignatura\":\"Matematicas\",\"fecha\":\"2024-05-10\",\"presente\":true}"
curl -s -X POST http://localhost:8082/api/v1/asistencias -H "Content-Type: application/json" ^
  -d "{\"studentId\":2,\"asignatura\":\"Matematicas\",\"fecha\":\"2024-05-11\",\"presente\":true}"
curl -s -X POST http://localhost:8082/api/v1/asistencias -H "Content-Type: application/json" ^
  -d "{\"studentId\":2,\"asignatura\":\"Matematicas\",\"fecha\":\"2024-05-12\",\"presente\":true}"
curl -s -X POST http://localhost:8082/api/v1/asistencias -H "Content-Type: application/json" ^
  -d "{\"studentId\":2,\"asignatura\":\"Matematicas\",\"fecha\":\"2024-05-13\",\"presente\":false,\"justificacion\":\"Certificado medico\"}"
curl -s -X POST http://localhost:8082/api/v1/asistencias -H "Content-Type: application/json" ^
  -d "{\"studentId\":2,\"asignatura\":\"Matematicas\",\"fecha\":\"2024-05-14\",\"presente\":false}"
```

### Paso 4 — Generar reportes y verificar alertas

```bash
REM Alumno 1 — debe ser SIN_ALERTA
curl -s http://localhost:8082/api/v1/reportes/estudiante/1 | python -m json.tool

REM Alumno 2 — debe ser ALERTA_CRITICA
curl -s http://localhost:8082/api/v1/reportes/estudiante/2 | python -m json.tool
```

### Paso 5 — Mismo flujo vía Gateway

```bash
curl -s http://localhost:8080/api/v1/gestion/reportes/estudiante/1 | python -m json.tool
curl -s http://localhost:8080/api/v1/gestion/reportes/estudiante/2 | python -m json.tool
```

### Paso 6 — Enviar notificación al apoderado del alumno en riesgo

```bash
curl -s -X POST http://localhost:8085/api/notificaciones ^
  -H "Content-Type: application/json" ^
  -d "{\"emisor\":\"Sistema Academico\",\"destinatario\":\"Apoderado Carlos Mendez\",\"asunto\":\"ALERTA CRITICA — Riesgo de repitencia\",\"mensaje\":\"El estudiante Carlos Mendez (Curso 7) presenta promedio 2.75 y asistencia del 60%. Se requiere entrevista urgente con el docente.\",\"fechaEnvio\":\"2024-05-15T09:00:00\"}"
```

---

## 12. Errores frecuentes y soluciones

### ❌ `Connection refused` al llamar al MS

**Causa:** El MS no levantó o levantó en otro puerto.
**Solución:**
```bash
# Verificar qué está corriendo en el puerto
netstat -ano | findstr :8082
netstat -ano | findstr :8080
```

---

### ❌ `Failed to configure a DataSource`

**Causa:** No puede conectar a PostgreSQL.
**Solución:** Verificar que las variables de entorno estén bien seteadas:
```bash
echo %DB_URL%
echo %DB_USERNAME%
```

---

### ❌ `Table 'estudiantes' doesn't exist`

**Causa:** JPA está en modo `validate` y las tablas no existen.
**Solución:** Cambiar a `create-drop` para la primera ejecución:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.jpa.hibernate.ddl-auto=create-drop"
```

---

### ❌ Gateway retorna `503` siempre (no solo en fallback)

**Causa:** Eureka está habilitado y no puede conectar al servidor.
**Solución:** Deshabilitar Eureka para pruebas locales:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
# (con el archivo application-local.yml que apunta a localhost:8082 directamente)
```

---

### ❌ `IllegalArgumentException: Nota inválida` al crear nota

**Causa:** La nota está fuera del rango 1.0–7.0.
**Respuesta esperada (400):**
```json
{
  "title": "Argumento inválido",
  "status": 400,
  "detail": "Nota inválida: 8.0. Debe estar entre 1.0 y 7.0"
}
```

---

### ❌ `404 Not Found` en el Gateway

**Causa:** La ruta no coincide con ningún predicado.
**Verificar:** Las rutas configuradas son:
- `/api/v1/gestion/**` → ms-academico (con rewrite)
- `/api/v1/estudiantes/**` → ms-academico (directo)
- `/api/v1/notas/**` → ms-academico (directo)
- `/api/v1/asistencias/**` → ms-academico (directo)
- `/api/v1/reportes/**` → ms-academico (directo)
- `/api/v1/comunicaciones/**` → ms-comunicaciones (con rewrite)

---

### Ejecutar tests unitarios y ver cobertura JaCoCo

```bash
cd "C:\Duoc\Proyecto FS3\backend\ms-academico"

# Ejecutar tests
mvn test

# Generar reporte HTML de cobertura
mvn test jacoco:report

# Abrir reporte (Windows)
start target\site\jacoco\index.html
```

---

*Guía generada para el Proyecto Fullstack III — Colegio Bernardo O'Higgins*
*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*
