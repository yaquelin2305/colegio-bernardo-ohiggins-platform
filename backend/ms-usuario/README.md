# MS-Usuario — Autenticación y Autorización
## Colegio Bernardo O'Higgins | FS3 DUOC

Microservicio responsable de la autenticación y autorización de todos los actores del sistema educativo: **Docentes**, **Apoderados**, **Estudiantes** y **Administradores**.

---

## Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────┐
│                    INFRAESTRUCTURA                           │
│  ┌─────────────┐                     ┌───────────────────┐  │
│  │ REST        │                     │ PostgreSQL (JPA)   │  │
│  │ Controller  │                     │ UsuarioRepository  │  │
│  │ (Adaptador  │                     │ Adapter           │  │
│  │  entrada)   │                     │ (Adaptador salida) │  │
│  └──────┬──────┘                     └─────────┬─────────┘  │
│         │                                      │             │
│  ┌──────▼──────────────────────────────────────▼──────────┐ │
│  │                  APLICACIÓN                             │ │
│  │  ┌──────────────┐     ┌──────────────────────────────┐ │ │
│  │  │ LoginUseCase │     │ RegistroUseCase              │ │ │
│  │  │ (Puerto in)  │     │ (Puerto in)                  │ │ │
│  │  └──────┬───────┘     └──────────────────────────────┘ │ │
│  │         │                                               │ │
│  │  ┌──────▼───────────────────────────────────────────┐  │ │
│  │  │           UserStrategyFactory                    │  │ │
│  │  │  crear(rol) → AuthorizationStrategy              │  │ │
│  │  └──────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                    DOMINIO                              │ │
│  │  Usuario | RolUsuario | Permisos | Excepciones          │ │
│  │  (CERO dependencias de frameworks)                      │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## Patrón Strategy — Autorización por Rol

### Diagrama UML

```
              «interface»
         AuthorizationStrategy
         ┌─────────────────────────────┐
         │ + resolverPermisos(usuario) │
         │ + generarClaimsAdicionales()│
         └─────────────┬───────────────┘
                       │
          ┌────────────┼─────────────────┬────────────────┐
          │            │                 │                │
  ┌───────▼──────┐ ┌───▼──────────┐ ┌───▼──────────┐ ┌───▼──────────┐
  │   Docente    │ │  Apoderado   │ │  Estudiante  │ │    Admin     │
  │ Strategy     │ │  Strategy    │ │  Strategy    │ │  Strategy    │
  ├──────────────┤ ├──────────────┤ ├──────────────┤ ├──────────────┤
  │GET, POST, PUT│ │  Solo GET    │ │  Solo GET    │ │GET,POST,PUT  │
  │notas         │ │notas         │ │notas         │ │DELETE        │
  │asistencias   │ │asistencias   │ │asistencias   │ │todos recursos│
  │estudiantes   │ │reportes      │ │              │ │              │
  │cursos        │ │soloLectura:T │ │soloLectura:T │ │soloLectura:F │
  │soloLectura:F │ │claim:pupiloId│ │claim:alum.Id │ │              │
  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
```

### ¿Por qué Strategy?

El comportamiento de "qué puede ver o hacer un usuario" **varía por rol**. Sin Strategy, el código tendría cadenas de `if/else` distribuidas por todo el sistema. Con Strategy:

- **Open/Closed**: Agregar un nuevo rol (ej: INSPECTOR) no modifica el código existente.
- **Single Responsibility**: Cada clase de Strategy tiene una única responsabilidad.
- **Testabilidad**: Cada Strategy se prueba de forma aislada.

---

## Patrón Factory Method — UserStrategyFactory

```
        «component»
    UserStrategyFactory
    ┌─────────────────────────────┐
    │ + crear(RolUsuario): Strategy│
    └──────────────┬──────────────┘
                   │
     switch(rol) ──┤
                   ├── DOCENTE    → new DocenteAuthorizationStrategy()
                   ├── APODERADO  → new ApoderadoAuthorizationStrategy()
                   ├── ESTUDIANTE → new EstudianteAuthorizationStrategy()
                   └── ADMIN      → new AdminAuthorizationStrategy()
```

### ¿Por qué Factory Method?

El `LoginUseCase` necesita una Strategy sin conocer su clase concreta. La fábrica:

1. **Centraliza** la lógica de instanciación.
2. **Desacopla** el caso de uso de las implementaciones concretas.
3. **Facilita** agregar nuevas estrategias sin tocar el código cliente.

---

## Flujo de Autenticación

```
  Cliente           Gateway            MS-Usuario          BD
     │                 │                    │               │
     │─── POST /login ─►│                    │               │
     │                 │── reenvía ─────────►│               │
     │                 │                    │─ buscarPorEmail►│
     │                 │                    │◄── Usuario ────│
     │                 │                    │                │
     │                 │          [verifica contraseña BCrypt]
     │                 │                    │                │
     │                 │          [Factory.crear(rol)]
     │                 │                    │                │
     │                 │          [Strategy.resolverPermisos()]
     │                 │                    │                │
     │                 │          [JWT con claims de rol]
     │                 │                    │                │
     │                 │◄── JWT + permisos ─│                │
     │◄── 200 OK ──────│                    │                │
```

---

## JWT — Claims Personalizados

El token JWT generado incluye claims específicos según el rol:

### DOCENTE
```json
{
  "sub": "docente@colegio.cl",
  "rol": "DOCENTE",
  "perfilId": 42,
  "recursos": ["notas", "asistencias", "estudiantes", "cursos"],
  "soloLectura": false
}
```

### APODERADO (protección de datos del menor)
```json
{
  "sub": "apoderado@colegio.cl",
  "rol": "APODERADO",
  "pupiloId": 99,
  "recursos": ["notas", "asistencias", "reportes-academicos"],
  "soloLectura": true
}
```
> ⚠️ El claim `pupiloId` es la restricción central: el MS-Académico filtra **todas** las consultas usando este valor.

### ESTUDIANTE
```json
{
  "sub": "alumno@colegio.cl",
  "rol": "ESTUDIANTE",
  "estudianteId": 55,
  "recursos": ["notas", "asistencias"],
  "soloLectura": true
}
```

---

## 🔐 Protección Ética de Datos de Menores

### Marco Legal Aplicable

- **Ley N° 19.628** (Chile): Protección de la vida privada y datos de carácter personal.
- **Ley N° 20.536**: Sobre violencia escolar — obligación de confidencialidad de datos de menores.

### Principios Implementados

| Principio | Implementación |
|-----------|---------------|
| **Mínimo Privilegio** | Cada rol accede SOLO a los recursos que necesita |
| **Separación de Datos** | El `pupiloId` restringe el acceso a nivel de BD |
| **Solo Lectura para Apoderados** | No pueden modificar notas ni asistencias |
| **Aislamiento por Identidad** | Un estudiante solo ve SUS datos (`estudianteId`) |
| **Auditoría** | Todos los accesos quedan registrados en logs |
| **Soft Delete** | Los usuarios no se eliminan, se desactivan |

### ¿Por qué esto es crítico?

Un sistema escolar maneja datos de menores de edad. Si un apoderado pudiera ver datos de otros estudiantes, o si un estudiante pudiera modificar sus notas, el impacto sería:
- **Legal**: Violación de la Ley 19.628.
- **Ético**: Exposición de información sensible de menores.
- **Operacional**: Pérdida de integridad académica.

La arquitectura Strategy + JWT Claims garantiza que estas restricciones son **estructurales**, no opcionales.

---

## Guía de Integración con Supabase Auth

Supabase Auth puede usarse como proveedor de autenticación externo. Para integrarlo:

### 1. Configuración en `application.yml`

```yaml
supabase:
  url: ${SUPABASE_URL}
  anon-key: ${SUPABASE_ANON_KEY}
  service-role-key: ${SUPABASE_SERVICE_ROLE_KEY}
```

### 2. Crear adaptador SupabaseAuthAdapter

```java
@Component
@ConditionalOnProperty("supabase.url")
public class SupabaseAuthAdapter implements PasswordEncoderPort {
    // Delegar el hash a Supabase en lugar de BCrypt local
    // POST https://{project}.supabase.co/auth/v1/signup
}
```

### 3. Flujo con Supabase

```
MS-Usuario → POST /auth/v1/signup (Supabase) → retorna user.id
MS-Usuario → guarda user.id en tabla usuarios con rol asignado
MS-Usuario → genera JWT propio con claims de rol (NO usa el JWT de Supabase)
```

> **Importante**: El JWT final SIEMPRE lo genera el MS-Usuario (con los claims de rol y permisos). Supabase solo maneja la verificación de identidad (email/password).

---

## Endpoints

| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| `POST` | `/api/v1/auth/login` | No | Autenticar usuario |
| `POST` | `/api/v1/auth/register` | No | Registrar nuevo usuario |
| `GET` | `/api/v1/auth/health` | No | Health check |

### Ejemplo: Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "docente@colegio.cl",
    "password": "password123"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "email": "docente@colegio.cl",
  "nombreCompleto": "Carlos Rodríguez",
  "rol": "DOCENTE",
  "permisos": ["notas", "asistencias", "estudiantes", "cursos"],
  "expiraEn": 1714298400000
}
```

---

## Variables de Entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `DB_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/usuario_db` |
| `DB_USERNAME` | Usuario BD | `usuario` |
| `DB_PASSWORD` | Contraseña BD | `usuario123` |
| `JWT_SECRET` | Secreto para firmar JWT (mín. 32 chars) | ⚠️ Cambiar en prod |
| `EUREKA_URL` | URL de Eureka | `http://localhost:8761/eureka` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo (`dev` o `prod`) | `dev` |

---

## Ejecución Local

```bash
# Levantar todo el stack
docker compose up --build

# Solo el MS-Usuario con su BD
docker compose up postgres-usuario ms-usuario

# Tests con cobertura
cd backend/ms-usuario
mvn test jacoco:report
# Reporte en: target/site/jacoco/index.html
```

---

## Estructura del Proyecto

```
ms-usuario/
├── src/
│   ├── main/java/cl/duoc/colegio/usuario/
│   │   ├── domain/
│   │   │   ├── model/          ← Usuario, RolUsuario, Permisos
│   │   │   ├── port/
│   │   │   │   ├── in/         ← LoginUseCase, RegistroUseCase
│   │   │   │   └── out/        ← RepositoryPort, TokenPort, PasswordPort
│   │   │   └── exception/      ← Excepciones de dominio
│   │   ├── application/
│   │   │   ├── usecase/        ← LoginUseCaseImpl, RegistroUseCaseImpl
│   │   │   ├── strategy/       ← Strategy por rol
│   │   │   ├── factory/        ← UserStrategyFactory
│   │   │   └── dto/            ← Request/Response DTOs
│   │   └── infrastructure/
│   │       ├── adapter/
│   │       │   ├── in/rest/    ← AuthController, GlobalExceptionHandler
│   │       │   └── out/
│   │       │       ├── persistence/ ← JPA Adapter
│   │       │       └── security/    ← JWT + BCrypt Adapters
│   │       └── config/         ← SecurityConfig (dev/prod)
│   └── resources/
│       ├── application.yml
│       └── db/init-usuario.sql
└── Dockerfile
```
