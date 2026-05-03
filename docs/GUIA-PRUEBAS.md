# 🧪 Guía de Pruebas — Plataforma Colegio Bernardo O'Higgins

> Guía completa para levantar, probar y verificar todos los microservicios del sistema.
> Incluye **MS-Académico**, **MS-Usuario**, **MS-Comunicaciones** y el **API Gateway**.

---

## Índice

1. [Requisitos previos](#1-requisitos-previos)
2. [Orden de arranque](#2-orden-de-arranque)
3. [Docker Compose — stack completo](#3-docker-compose--stack-completo)
4. [Levantar servicios individualmente](#4-levantar-servicios-individualmente)
5. [Verificar que todo está vivo](#5-verificar-que-todo-está-vivo)
6. [Auth — Login y Registro](#6-auth--login-y-registro)
7. [CURLs — MS-Académico directo (sin Gateway)](#7-curls--ms-académico-directo-sin-gateway)
8. [CURLs — MS-Académico vía API Gateway](#8-curls--ms-académico-vía-api-gateway)
9. [CURLs — MS-Comunicaciones vía API Gateway](#9-curls--ms-comunicaciones-vía-api-gateway)
10. [Flujo de prueba completo de punta a punta](#10-flujo-de-prueba-completo-de-punta-a-punta)
11. [Probar el Circuit Breaker](#11-probar-el-circuit-breaker)
12. [Pruebas unitarias y cobertura JaCoCo](#12-pruebas-unitarias-y-cobertura-jacoco)
13. [Errores frecuentes y soluciones](#13-errores-frecuentes-y-soluciones)

---

## 1. Requisitos previos

| Herramienta | Versión mínima | Verificar con |
|---|---|---|
| Java | 17 | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Docker Desktop | Cualquier | `docker --version` |
| Node.js | 20+ | `node -v` |
| cURL | Cualquier | `curl --version` |

---

## 2. Orden de arranque

> ⚠️ El orden importa. El API Gateway necesita que MS-Académico y MS-Usuario estén listos.

```
Orden recomendado:
  1. postgres          → :5432  (BD académica)
  2. postgres-usuario  → :5433  (BD de usuarios)
  3. ms-academico      → :8082
  4. ms-usuario        → :8083
  5. ms-comunicaciones → :8085
  6. api-gateway       → :8080
  7. frontend          → :5173  (opcional para pruebas de API)
```

---

## 3. Docker Compose — stack completo

**La forma más rápida de levantar todo el backend:**

```bash
# Desde la raíz del proyecto
docker compose up --build

# Verificar que los 4 servicios están UP
docker compose ps
```

Servicios levantados:
- `postgres` → BD académica en `:5432`
- `postgres-usuario` → BD de usuarios en `:5433`
- `ms-academico` → API académica en `:8082`
- `ms-usuario` → Auth y autorización en `:8083`
- `api-gateway` → Punto de entrada en `:8080`

---

## 4. Levantar servicios individualmente

### MS-Académico

```bash
cd "C:\Duoc\Proyecto FS3\backend\ms-academico"

# Sin Eureka (más simple para pruebas locales)
set DB_URL=jdbc:postgresql://localhost:5432/academico_db
set DB_USERNAME=academico
set DB_PASSWORD=academico123
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### MS-Usuario

```bash
cd "C:\Duoc\Proyecto FS3\backend\ms-usuario"

set DB_URL=jdbc:postgresql://localhost:5433/usuario_db
set DB_USERNAME=usuario
set DB_PASSWORD=usuario123
set JWT_SECRET=colegio-bernardo-ohiggins-secret-key-2024-duoc-fs3-very-long-secret
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### MS-Comunicaciones

```bash
cd "C:\Duoc\Proyecto FS3\backend\ms-comunicaciones"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### API Gateway

```bash
cd "C:\Duoc\Proyecto FS3\backend\api-gateway"

# Perfil local: sin Eureka, apunta directo a localhost
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

### Frontend

```bash
cd "C:\Duoc\Proyecto FS3\frontend"
npm install
npm run dev
# Abre en: http://localhost:5173
```

---

## 5. Verificar que todo está vivo

```bash
# MS-Usuario
curl -s http://localhost:8083/api/v1/auth/health

# MS-Académico
curl -s http://localhost:8082/actuator/health | findstr "status"

# MS-Comunicaciones
curl -s http://localhost:8085/actuator/health | findstr "status"

# API Gateway
curl -s http://localhost:8080/actuator/health | findstr "status"
```

Respuesta esperada: `{"status":"UP"}`

---

## 6. Auth — Login y Registro

### Usuario ADMIN inicial (creado por init-usuario.sql)

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@colegio-ohiggins.cl\",\"password\":\"Admin1234!\"}" ^
  | python -m json.tool
```

**Respuesta esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "email": "admin@colegio-ohiggins.cl",
  "nombreCompleto": "Administrador Sistema",
  "rol": "ADMIN",
  "permisos": ["notas","asistencias","estudiantes","docentes","usuarios","configuracion"],
  "expiraEn": 1714384800000
}
```

### Registrar un nuevo docente

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"docente@colegio.cl\",\"password\":\"Docente123!\",\"nombre\":\"Carlos\",\"apellido\":\"Rodriguez\",\"rol\":\"DOCENTE\"}" ^
  | python -m json.tool
```

### Registrar un apoderado (con pupiloId)

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"apoderado@mail.cl\",\"password\":\"Apoderado123!\",\"nombre\":\"Maria\",\"apellido\":\"Gonzalez\",\"rol\":\"APODERADO\",\"perfilId\":1}" ^
  | python -m json.tool
```

> `perfilId` vincula al apoderado con el ID del estudiante en MS-Académico.
> El JWT generado incluirá el claim `pupiloId: 1` que restricta todas sus consultas.

### Registrar un estudiante

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"alumno@colegio.cl\",\"password\":\"Alumno123!\",\"nombre\":\"Pedro\",\"apellido\":\"Lopez\",\"rol\":\"ESTUDIANTE\",\"perfilId\":1}" ^
  | python -m json.tool
```

### Login con credenciales incorrectas (debe devolver 401)

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@colegio-ohiggins.cl\",\"password\":\"incorrecta\"}" ^
  -w "\nHTTP Status: %{http_code}\n"
```

---

## 7. CURLs — MS-Académico directo (sin Gateway)

> Puerto: **8082** — Útil para probar el MS aislado

### 👤 ESTUDIANTES

#### Crear estudiante

```bash
curl -s -X POST http://localhost:8082/api/v1/estudiantes ^
  -H "Content-Type: application/json" ^
  -d "{\"rut\":\"12345678-9\",\"nombre\":\"Juan\",\"apellido\":\"Perez\",\"curso\":8}" ^
  | python -m json.tool
```

#### Obtener todos los estudiantes

```bash
curl -s http://localhost:8082/api/v1/estudiantes | python -m json.tool
```

#### Obtener por RUT

```bash
curl -s http://localhost:8082/api/v1/estudiantes/rut/12345678-9 | python -m json.tool
```

#### Listar por curso

```bash
curl -s http://localhost:8082/api/v1/estudiantes/curso/8 | python -m json.tool
```

---

### 📚 NOTAS

#### Registrar nota

```bash
curl -s -X POST http://localhost:8082/api/v1/notas ^
  -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Matematicas\",\"nota\":5.5,\"tipo\":\"PRUEBA\",\"fecha\":\"2024-05-15\",\"descripcion\":\"Prueba unidad 1\"}" ^
  | python -m json.tool
```

> Tipos válidos: `PRUEBA`, `TAREA`, `EXAMEN`, `TRABAJO`  
> Nota válida: entre `1.0` y `7.0`

#### Listar notas de un estudiante

```bash
curl -s http://localhost:8082/api/v1/notas/estudiante/1 | python -m json.tool
```

#### Calcular promedio general

```bash
curl -s http://localhost:8082/api/v1/notas/estudiante/1/promedio | python -m json.tool
```

#### Calcular promedio por asignatura

```bash
curl -s "http://localhost:8082/api/v1/notas/estudiante/1/promedio/asignatura/Matematicas" | python -m json.tool
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

#### Registrar ausencia con justificación

```bash
curl -s -X POST http://localhost:8082/api/v1/asistencias ^
  -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Historia\",\"fecha\":\"2024-05-16\",\"presente\":false,\"justificacion\":\"Certificado medico\"}" ^
  | python -m json.tool
```

#### Consultar porcentaje y riesgo de repitencia

```bash
curl -s http://localhost:8082/api/v1/asistencias/estudiante/1/porcentaje | python -m json.tool
```

> ⚠️ `enRiesgoRepitencia: true` cuando asistencia < 85%

---

### 📊 REPORTES

#### Generar reporte académico completo

```bash
curl -s http://localhost:8082/api/v1/reportes/estudiante/1 | python -m json.tool
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

> Puerto Gateway: **8080**

### Con perfil DEV (sin token)

```bash
# Listar estudiantes
curl -s http://localhost:8080/api/v1/gestion/estudiantes | python -m json.tool

# Notas de un estudiante
curl -s http://localhost:8080/api/v1/gestion/notas/estudiante/1 | python -m json.tool

# Reporte completo
curl -s http://localhost:8080/api/v1/gestion/reportes/estudiante/1 | python -m json.tool
```

### Con perfil PROD (con token JWT)

```bash
# 1. Obtener token via MS-Usuario
curl -s -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"docente@colegio.cl\",\"password\":\"Docente123!\"}"

# 2. Copiar el token y usarlo en la siguiente request
set TOKEN=eyJhbGciOiJIUzI1NiJ9...

curl -s http://localhost:8080/api/v1/gestion/notas/estudiante/1 ^
  -H "Authorization: Bearer %TOKEN%" ^
  | python -m json.tool
```

**Sin token → 401 RFC 7807:**
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

## 9. CURLs — MS-Comunicaciones vía API Gateway

### Directo al MS (puerto 8085)

```bash
# Enviar notificación
curl -s -X POST http://localhost:8085/api/notificaciones ^
  -H "Content-Type: application/json" ^
  -d "{\"emisor\":\"Docente Juan\",\"destinatario\":\"Apoderado Maria\",\"asunto\":\"Alerta rendimiento\",\"mensaje\":\"Su hijo presenta promedio bajo 4.0.\",\"fechaEnvio\":\"2024-05-15T10:00:00\"}" ^
  | python -m json.tool

# Listar notificaciones
curl -s http://localhost:8085/api/notificaciones | python -m json.tool
```

### Vía API Gateway (puerto 8080)

```bash
# Enviar vía Gateway
curl -s -X POST http://localhost:8080/api/v1/comunicaciones/notificaciones ^
  -H "Content-Type: application/json" ^
  -d "{\"emisor\":\"Sistema\",\"destinatario\":\"Apoderado Pedro\",\"asunto\":\"Inasistencia\",\"mensaje\":\"Su hijo estuvo ausente hoy.\",\"fechaEnvio\":\"2024-05-15T08:00:00\"}" ^
  | python -m json.tool

# Listar vía Gateway
curl -s http://localhost:8080/api/v1/comunicaciones/notificaciones | python -m json.tool
```

---

## 10. Flujo de prueba completo de punta a punta

### Paso 1 — Registrar usuarios

```bash
# Registrar docente
curl -s -X POST http://localhost:8080/api/v1/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"docente@colegio.cl\",\"password\":\"Docente123!\",\"nombre\":\"Carlos\",\"apellido\":\"Rodriguez\",\"rol\":\"DOCENTE\"}"

# Registrar apoderado (vinculado al estudiante con ID 1)
curl -s -X POST http://localhost:8080/api/v1/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"apoderado@mail.cl\",\"password\":\"Apod123!\",\"nombre\":\"Maria\",\"apellido\":\"Torres\",\"rol\":\"APODERADO\",\"perfilId\":1}"
```

### Paso 2 — Crear estudiantes en MS-Académico

```bash
curl -s -X POST http://localhost:8082/api/v1/estudiantes ^
  -H "Content-Type: application/json" ^
  -d "{\"rut\":\"11111111-1\",\"nombre\":\"Ana\",\"apellido\":\"Torres\",\"curso\":7}"

curl -s -X POST http://localhost:8082/api/v1/estudiantes ^
  -H "Content-Type: application/json" ^
  -d "{\"rut\":\"22222222-2\",\"nombre\":\"Carlos\",\"apellido\":\"Mendez\",\"curso\":7}"
```

### Paso 3 — Registrar notas

```bash
REM Alumno 1 — notas altas
curl -s -X POST http://localhost:8082/api/v1/notas -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Matematicas\",\"nota\":6.5,\"tipo\":\"PRUEBA\",\"fecha\":\"2024-05-10\"}"
curl -s -X POST http://localhost:8082/api/v1/notas -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"asignatura\":\"Historia\",\"nota\":5.8,\"tipo\":\"EXAMEN\",\"fecha\":\"2024-05-11\"}"

REM Alumno 2 — notas bajas (riesgo)
curl -s -X POST http://localhost:8082/api/v1/notas -H "Content-Type: application/json" ^
  -d "{\"studentId\":2,\"asignatura\":\"Matematicas\",\"nota\":2.5,\"tipo\":\"PRUEBA\",\"fecha\":\"2024-05-10\"}"
curl -s -X POST http://localhost:8082/api/v1/notas -H "Content-Type: application/json" ^
  -d "{\"studentId\":2,\"asignatura\":\"Historia\",\"nota\":3.0,\"tipo\":\"EXAMEN\",\"fecha\":\"2024-05-11\"}"
```

### Paso 4 — Registrar asistencias

```bash
REM Alumno 2 — baja asistencia (60%)
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

### Paso 5 — Generar reportes y verificar alertas

```bash
REM Alumno 1 — debe ser SIN_ALERTA
curl -s http://localhost:8082/api/v1/reportes/estudiante/1 | python -m json.tool

REM Alumno 2 — debe ser ALERTA_CRITICA
curl -s http://localhost:8082/api/v1/reportes/estudiante/2 | python -m json.tool
```

### Paso 6 — Mismo flujo vía Gateway (prod con token)

```bash
REM Login como docente
curl -s -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"docente@colegio.cl\",\"password\":\"Docente123!\"}"

REM Usar el token para ver los reportes
curl -s http://localhost:8080/api/v1/gestion/reportes/estudiante/2 ^
  -H "Authorization: Bearer <TOKEN_AQUI>" ^
  | python -m json.tool
```

### Paso 7 — Notificar al apoderado

```bash
curl -s -X POST http://localhost:8085/api/notificaciones ^
  -H "Content-Type: application/json" ^
  -d "{\"emisor\":\"Sistema Academico\",\"destinatario\":\"Apoderado Carlos Mendez\",\"asunto\":\"ALERTA CRITICA\",\"mensaje\":\"El estudiante Carlos Mendez presenta promedio 2.75 y asistencia 60%. Se requiere entrevista urgente.\",\"fechaEnvio\":\"2024-05-15T09:00:00\"}"
```

---

## 11. Probar el Circuit Breaker

### Simular caída del MS-Académico

```bash
# 1. Detener el MS-Académico (Ctrl+C o docker compose stop ms-academico)

# 2. Petición al Gateway — debe retornar fallback 503
curl -s http://localhost:8080/api/v1/gestion/estudiantes | python -m json.tool
```

**Respuesta esperada:**
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

### Simular caída del MS-Usuario

```bash
# Detener ms-usuario y probar login
curl -s -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@colegio-ohiggins.cl\",\"password\":\"Admin1234!\"}" ^
  -w "\nHTTP Status: %{http_code}\n"
```

**Respuesta esperada (503):**
```json
{
  "title": "Servicio de autenticación no disponible",
  "status": 503,
  "service": "ms-usuario"
}
```

---

## 12. Pruebas unitarias y cobertura JaCoCo

### MS-Usuario

```bash
cd "C:\Duoc\Proyecto FS3\backend\ms-usuario"

# Ejecutar tests
mvn test

# Generar reporte HTML de cobertura
mvn test jacoco:report

# Abrir reporte (Windows)
start target\site\jacoco\index.html
```

**Tests implementados:**

| Clase | Casos | Cubre |
|---|---|---|
| `AuthorizationStrategyTest` | 15+ | Permisos por rol, claims JWT, restricciones de lectura |
| `UserStrategyFactoryTest` | 6 | Instancia correcta por rol, `@ParameterizedTest` con todos los roles |
| `LoginUseCaseImplTest` | 6 | Flujo exitoso, usuario no encontrado, password incorrecto, usuario inactivo |
| `JwtTokenAdapterTest` | 6 | Generación, validación, extracción de email, tokens inválidos |

### MS-Académico

```bash
cd "C:\Duoc\Proyecto FS3\backend\ms-academico"
mvn test
mvn test jacoco:report
start target\site\jacoco\index.html
```

**Tests implementados:**

| Clase | Cubre |
|---|---|
| `StudentTest` | Cálculo de promedios, asistencia, riesgo de repitencia |
| `GradeTest` | Validación de notas (1.0–7.0), aprobación |
| `GradeServiceTest` | Casos de uso: registrar, calcular promedio, eliminar |
| `AttendanceServiceTest` | Porcentaje de asistencia, detección de riesgo |
| `ReportServiceTest` | Generación de reportes y tipos de alerta |

> Ambos proyectos fallan el build si la cobertura de líneas es **menor al 60%** (configurado en JaCoCo).

---

## 13. Errores frecuentes y soluciones

### ❌ `Connection refused` en algún MS

```bash
# Verificar puertos en uso
netstat -ano | findstr :8080
netstat -ano | findstr :8082
netstat -ano | findstr :8083
netstat -ano | findstr :8085
```

---

### ❌ `Failed to configure a DataSource`

**Causa:** Variables de entorno de BD no seteadas.

```bash
# Verificar variables
echo %DB_URL%
echo %DB_USERNAME%
echo %DB_PASSWORD%
```

---

### ❌ `Table 'usuarios' doesn't exist`

**Causa:** El script `init-usuario.sql` no se ejecutó.  
**Solución:** Cambiar `ddl-auto` a `update` en el primer arranque:

```bash
set SPRING_JPA_HIBERNATE_DDL-AUTO=update
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

### ❌ Gateway retorna `503` siempre

**Causa:** Eureka está habilitado y no puede conectar.  
**Solución:** Usar el perfil `local`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

---

### ❌ `401 Unauthorized` en un endpoint que debería ser público

**Causa:** Estás usando perfil `prod` en el Gateway.  
**Solución para desarrollo:** Cambiar a perfil `dev`:

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

---

### ❌ `CredencialesInvalidasException` al hacer login

**Causas posibles:**
1. Email no registrado en la BD.
2. Password incorrecto (BCrypt no coincide).
3. Usuario inactivo.

```bash
# Verificar que el usuario existe con el admin
curl -s -X POST http://localhost:8083/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@colegio-ohiggins.cl\",\"password\":\"Admin1234!\"}"
```

---

### ❌ `IllegalArgumentException: Nota inválida`

**Causa:** La nota está fuera del rango 1.0–7.0.

```json
{
  "title": "Argumento inválido",
  "status": 400,
  "detail": "Nota inválida: 8.0. Debe estar entre 1.0 y 7.0"
}
```

---

*Guía generada para el Proyecto Fullstack III — Colegio Bernardo O'Higgins*  
*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*
