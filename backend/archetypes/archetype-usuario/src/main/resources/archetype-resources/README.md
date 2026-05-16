# ${artifactId} — Microservicio de Autenticación

> Generado con el arquetipo **Usuario/Auth** — Colegio Bernardo O'Higgins

Microservicio de autenticación por RUT chileno con autorización basada en roles.

## Patrones implementados

| Patrón | Dónde | Propósito |
|---|---|---|
| Strategy | `AuthorizationStrategy` | Permisos por rol sin if/else |
| Factory Method | `AuthorizationStrategyFactory` | Creación centralizada de Strategy |
| Hexagonal | Capas domain/application/infrastructure | Independencia del framework |
| Repository | `UsuarioRepositoryPort` | Abstracción de persistencia |
| DTO | LoginRequestDto, AuthResponseDto | Transporte inmutable de datos |

## Endpoints

| Método | Ruta | Auth |
|---|---|---|
| `POST` | `/api/v1/auth/login` | Público |
| `GET` | `/api/v1/auth/health` | Público |

## Ejecución

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
DB_URL=jdbc:postgresql://localhost:5432/colegio_db \
DB_USERNAME=colegio DB_PASSWORD=colegio123 \
JWT_SECRET=clave-secreta-de-32-caracteres-minimo \
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Tests
mvn test
```

## Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"rut":"99888777-6","password":"Admin2Test!"}'
```

Respuesta:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "rut": "99888777-6",
  "nombreCompleto": "Admin Backup",
  "rol": "ADMIN",
  "permisos": ["notas","asistencias","..."],
  "expiraEn": 1778725831239
}
```
