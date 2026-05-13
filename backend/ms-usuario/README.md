# 👤 MS-Usuario — Autenticación y Autorización

> Colegio Bernardo O'Higgins | Proyecto Fullstack III — Duoc UC

Microservicio de autenticación por **RUT chileno** y autorización basada en roles (ADMIN, DOCENTE, APODERADO, ESTUDIANTE).

---

## Índice

- [Arquitectura Hexagonal](#arquitectura-hexagonal)
- [Patrón Strategy — Autorización por Rol](#patrón-strategy--autorización-por-rol)
- [Patrón Factory Method](#patrón-factory-method)
- [Flujo de Autenticación](#flujo-de-autenticación)
- [JWT — Claims Personalizados](#jwt--claims-personalizados)
- [Endpoints API](#endpoints-api)
- [Variables de Entorno](#variables-de-entorno)
- [Ejecución Local](#ejecución-local)
- [Pruebas Unitarias y Cobertura](#pruebas-unitarias-y-cobertura)
- [Docker](#docker)

---

## Arquitectura Hexagonal

Este microservicio implementa **Arquitectura Hexagonal (Ports & Adapters)** estricta:

```
┌────────────────────────────────────────────────────────────────┐
│                      INFRAESTRUCTURA                            │
│  ┌──────────────┐                      ┌─────────────────────┐ │
│  │ REST API      │                      │ PostgreSQL / JPA     │ │
│  │ (Adaptador IN)│                      │ (Adaptador OUT)      │ │
│  └───────┬──────┘                      └──────────┬──────────┘ │
│          │                                         │             │
│   ┌──────▼─────────────────────────────────────────▼─────────┐ │
│   │                    APLICACIÓN                             │ │
│   │  LoginUseCase   RegistroUseCase   AdminUseCase            │ │
│   │  ┌────────────────────────────────────────────────────┐  │ │
│   │  │  UserStrategyFactory → AuthorizationStrategy        │  │ │
│   │  └────────────────────────────────────────────────────┘  │ │
│   └──────────────────────────────────────────────────────────┘ │
│                              │                                  │
│   ┌──────────────────────────▼───────────────────────────────┐ │
│   │                      DOMINIO                              │ │
│   │  Usuario · RolUsuario · Permisos · Excepciones            │ │
│   │  (CERO dependencias de frameworks)                        │ │
│   └──────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────┘
```

### Capas y responsabilidades

| Capa | Paquete | Contenido |
|---|---|---|
| **Domain** | `domain.model` | Entidades puras: Usuario, RolUsuario |
| **Domain** | `domain.port.in` | Puertos de entrada: LoginUseCase, RegistroUseCase |
| **Domain** | `domain.port.out` | Puertos de salida: UsuarioRepositoryPort, TokenPort |
| **Domain** | `domain.exception` | CredencialesInvalidasException, UsuarioInactivoException |
| **Application** | `application.usecase` | Implementaciones: LoginUseCaseImpl, RegistroUseCaseImpl |
| **Application** | `application.strategy` | Patrón Strategy: AdminStrategy, DocenteStrategy, etc. |
| **Application** | `application.factory` | UserStrategyFactory |
| **Application** | `application.dto` | LoginRequestDto, AuthResponseDto (Java `record`) |
| **Infrastructure** | `infrastructure.adapter.in.rest` | AuthController, AdminController, GlobalExceptionHandler |
| **Infrastructure** | `infrastructure.adapter.out.persistence` | JPA entities, Spring Data repos, adapters |
| **Infrastructure** | `infrastructure.adapter.out.security` | JwtTokenAdapter, BCryptPasswordAdapter |
| **Infrastructure** | `infrastructure.config` | SecurityConfigProd, SecurityConfigDev |

---

## Patrón Strategy — Autorización por Rol

### ¿Por qué Strategy?

El comportamiento de "qué puede ver o hacer un usuario" **varía por rol**. Sin Strategy, el código tendría cadenas de `if/else` por todo el sistema.

```
              «interface»
         AuthorizationStrategy
         ┌─────────────────────────────┐
         │ + resolverPermisos(usuario) │
         │ + recursosDisponibles()     │
         └─────────────┬───────────────┘
                       │
          ┌────────────┼─────────────────┬────────────────┐
          │            │                 │                │
  ┌───────▼──────┐ ┌───▼──────────┐ ┌───▼──────────┐ ┌───▼──────────┐
  │   Docente    │ │  Apoderado   │ │  Estudiante  │ │    Admin     │
  │ Strategy     │ │  Strategy    │ │  Strategy    │ │  Strategy    │
  ├──────────────┤ ├──────────────┤ ├──────────────┤ ├──────────────┤
  │notas         │ │notas         │ │notas         │ │todos recursos│
  │asistencias   │ │asistencias   │ │asistencias   │ │notas, cursos │
  │estudiantes   │ │reportes      │ │soloLectura:T │ │asistencias   │
  │cursos        │ │soloLectura:T │ │              │ │usuarios      │
  │soloLectura:F │ │              │ │              │ │soloLectura:F │
  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
```

Beneficios:
- **Open/Closed**: Agregar un nuevo rol no modifica código existente
- **Single Responsibility**: Cada clase tiene una única responsabilidad
- **Testabilidad**: Cada Strategy se prueba de forma aislada

---

## Patrón Factory Method

Centraliza la creación de la Strategy correcta según el rol:

```java
@Component
public class UserStrategyFactory {
    public AuthorizationStrategy crear(RolUsuario rol) {
        return switch (rol) {
            case ADMIN      -> new AdminAuthorizationStrategy();
            case DOCENTE    -> new DocenteAuthorizationStrategy();
            case APODERADO  -> new ApoderadoAuthorizationStrategy();
            case ESTUDIANTE -> new EstudianteAuthorizationStrategy();
        };
    }
}
```

---

## Flujo de Autenticación

```
  Frontend           API Gateway           MS-Usuario              BD
     │                    │                    │                    │
     │── POST /login ────►│                    │                    │
     │  {rut, password}   │── reenvía ────────►│                    │
     │                    │                    │─ buscarPorRut() ──►│
     │                    │                    │◄── Usuario ────────│
     │                    │                    │                    │
     │                    │         [BCrypt.matches(password, hash)]
     │                    │                    │                    │
     │                    │         [Factory.crear(rol)]
     │                    │         [Strategy.getRecursos()]
     │                    │         [JWT con claims: sub=RUT, userId, role, recursos]
     │                    │                    │                    │
     │◄── 200 OK ─────────│◄── AuthResponseDto ─│                    │
     │  {accessToken,     │                    │                    │
     │   rut, nombre,     │                    │                    │
     │   rol, permisos}   │                    │                    │
```

### Credenciales de acceso

- **Identificador**: RUT chileno (formato `12345678-9`)
- **Contraseña**: BCrypt strength 12
- **Soft delete**: `activo=false` preserva integridad referencial

---

## JWT — Claims Personalizados

### Claims estándar del token

| Claim | Descripción | Ejemplo |
|---|---|---|
| `sub` | RUT del usuario | `99888777-6` |
| `userId` | UUID interno | `68d17785-f139-4600-80d9-12d28c180a74` |
| `email` | Email del usuario | `admin@colegio.cl` |
| `nombre` | Nombre completo | `Admin Backup` |
| `role` | Rol del usuario | `ADMIN` |
| `rol` | Alias español de `role` | `ADMIN` |
| `recursos` | Permisos por rol | `["notas","asistencias","cursos"]` |
| `soloLectura` | Acceso solo lectura | `false` |
| `iss` | Emisor del token | `ms-usuario` |
| `iat` | Fecha de emisión | timestamp |
| `exp` | Expiración (24h) | timestamp |

### Algoritmo

- **Firma**: HMAC-SHA256
- **TTL**: 24 horas
- **Biblioteca**: jjwt 0.11.5

---

## Endpoints API

### Auth (público — sin token)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/v1/auth/login` | Iniciar sesión (RUT + password) |
| `GET` | `/api/v1/auth/health` | Health check |

### Admin (requiere token + rol ADMIN)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/v1/admin/crear` | Crear usuario |
| `GET` | `/api/v1/admin/{id}` | Obtener por UUID |
| `GET` | `/api/v1/admin/listar/{rol}` | Listar por rol |
| `PUT` | `/api/v1/admin/actualizar/{id}` | Actualizar usuario |
| `DELETE` | `/api/v1/admin/eliminar/{id}` | Eliminar (soft delete) |

### Ejemplo: Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"rut":"99888777-6","password":"Admin2Test!"}'
```

**Respuesta 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "rut": "99888777-6",
  "nombreCompleto": "Admin Backup",
  "rol": "ADMIN",
  "permisos": ["notas","asistencias","estudiantes","docentes","apoderados","cursos","reportes-academicos","usuarios","configuracion"],
  "expiraEn": 1778725831239
}
```

**Respuesta 400 — validación:**
```json
{
  "status": 400,
  "mensaje": "Error de validación",
  "errores": {"rut": "Formato de RUT inválido (ej: 12345678-9)"},
  "timestamp": "2026-05-13T02:35:32"
}
```

**Respuesta 401 — credenciales inválidas:**
```json
{
  "status": 401,
  "mensaje": "Credenciales inválidas",
  "timestamp": "2026-05-13T02:35:32"
}
```

---

## Variables de Entorno

| Variable | Descripción | Default |
|---|---|---|
| `DB_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/colegio_db` |
| `DB_USERNAME` | Usuario BD | `colegio` |
| `DB_PASSWORD` | Contraseña BD | `colegio123` |
| `JWT_SECRET` | Clave HMAC-SHA256 (mín. 32 chars) | Requerido |
| `SPRING_PROFILES_ACTIVE` | Perfil (`dev`, `prod`) | `dev` |

---

## Ejecución Local

### Pre-requisitos

- Java 17+
- Maven 3.9+
- PostgreSQL (schema `users_schema`)

### Compilar y ejecutar

```bash
cd backend/ms-usuario

# Perfil dev (sin seguridad JWT)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Perfil prod (trust-the-gateway)
DB_URL=jdbc:postgresql://localhost:5432/colegio_db \
DB_USERNAME=colegio \
DB_PASSWORD=colegio123 \
JWT_SECRET=clave-secreta-de-32-caracteres-minimo \
SPRING_PROFILES_ACTIVE=prod \
mvn spring-boot:run
```

---

## Pruebas Unitarias y Cobertura

### Ejecutar pruebas

```bash
mvn test
```

### Reporte de cobertura JaCoCo

```bash
mvn verify
# Reporte HTML en: target/site/jacoco/index.html
```

### Tests implementados (37 tests ✅)

| Clase de Test | Cobertura |
|---|---|
| `LoginUseCaseImplTest` | Login por RUT, credenciales inválidas, usuario inactivo |
| `AdminUseCaseImplTest` | CRUD de usuarios, listado por rol |
| `BCryptPasswordAdapterTest` | Hash y verificación BCrypt |
| `JwtTokenAdapterTest` | Generación y validación JWT |
| `DomainModelTest` | Entidades de dominio |
| `AuthorizationStrategyTest` | Estrategias por rol |

---

## Docker

### Construir imagen

```bash
docker build -t ms-usuario:1.0.0 .
```

### Ejecutar contenedor

```bash
docker run -p 8083:8083 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/colegio_db \
  -e DB_USERNAME=colegio \
  -e DB_PASSWORD=colegio123 \
  -e JWT_SECRET=clave-secreta-de-32-caracteres-minimo \
  ms-usuario:1.0.0
```

### Con Docker Compose

```bash
docker compose -f docker-compose.test.yml up --build ms-usuario
```

---

## Estructura del Proyecto

```
ms-usuario/
├── src/
│   ├── main/java/cl/duoc/colegio/usuario/
│   │   ├── domain/
│   │   │   ├── model/          ← Usuario, RolUsuario
│   │   │   ├── port/
│   │   │   │   ├── in/         ← LoginUseCase, RegistroUseCase, AdminUseCase
│   │   │   │   └── out/        ← UsuarioRepositoryPort, TokenPort, PasswordEncoderPort
│   │   │   └── exception/      ← CredencialesInvalidasException, UsuarioInactivoException
│   │   ├── application/
│   │   │   ├── usecase/        ← LoginUseCaseImpl, RegistroUseCaseImpl, AdminUseCaseImpl
│   │   │   ├── strategy/       ← AdminStrategy, DocenteStrategy, ApoderadoStrategy, EstudianteStrategy
│   │   │   ├── factory/        ← UserStrategyFactory
│   │   │   └── dto/            ← LoginRequestDto, AuthResponseDto (Java records)
│   │   └── infrastructure/
│   │       ├── adapter/
│   │       │   ├── in/rest/    ← AuthController, AdminController, GlobalExceptionHandler
│   │       │   └── out/
│   │       │       ├── persistence/  ← JPA entities, Spring Data repos, UsuarioPersistenceAdapter
│   │       │       └── security/     ← JwtTokenAdapter, BCryptPasswordAdapter
│   │       └── config/         ← SecurityConfigProd (trust-the-gateway), SecurityConfigDev
│   ├── resources/
│   │   ├── application.yml
│   │   └── application-prod.yml
│   └── test/
├── Dockerfile
├── pom.xml
└── README.md
```

---

*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*
*Docente: Alexis Jacob Jiménez Parada — Desarrollo Fullstack III*