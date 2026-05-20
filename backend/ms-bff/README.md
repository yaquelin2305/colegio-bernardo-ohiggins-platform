# ms-bff — Backend For Frontend

Orquestador de microservicios para la plataforma educativa del Colegio Bernardo O'Higgins. Recibe peticiones del frontend y las resuelve coordinando llamadas a los microservicios core mediante OpenFeign.

## Stack

- Java 17 + Spring Boot 3.x
- Spring Cloud OpenFeign (comunicacion con MS)
- Spring Cloud Netflix Eureka Client (descubrimiento de servicios)
- Lombok
- Maven

## Puerto

`8084` (interno Docker: `8084`)

## Estructura

```
src/main/java/com/cbo/bff/
├── asistencia/         # controller, service, client, dto
├── calificaciones/     # controller, service, dto
├── comunicaciones/     # controller, service, client, dto
├── gestionacademica/   # controller, service, client, dto (boletin, dashboard)
├── config/             # FeignInterceptorConfig, GlobalExceptionHandler, JacksonConfig
└── controller/         # BffStatusController (GET /api/bff/status)
```

## Endpoints

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/bff/status` | Health check |
| GET | `/api/bff/dashboard/stats` | Estadisticas generales (solo ADMIN) |
| GET | `/api/bff/boletin/{estudianteId}` | Boletin de notas y asistencia |
| GET | `/api/bff/cursos` | Listado de cursos |
| GET | `/api/bff/asignaturas` | Listado de asignaturas |
| GET | `/api/bff/usuarios/{rol}` | Usuarios por rol |
| POST | `/api/bff/asistencia/registrar` | Registrar asistencia |
| GET | `/api/bff/asistencia/curso/{cursoId}` | Asistencia de un curso |
| GET | `/api/bff/asistencia/inasistencias` | Inasistencias pendientes |
| PATCH | `/api/bff/asistencia/{id}/justificar` | Justificar inasistencia |
| GET | `/api/bff/comunicaciones/bandeja/{usuarioId}` | Bandeja de mensajes |
| POST | `/api/bff/comunicaciones/enviar` | Enviar mensaje |
| PATCH | `/api/bff/comunicaciones/leido/{mensajeId}` | Marcar como leido |

## Microservicios consumidos

| FeignClient | Servicio | URL Docker |
|---|---|---|
| AcademicoFeignClient | ms-academico | `http://ms-academico:8082` |
| UsuarioFeignClient | ms-usuario | `http://ms-usuario:8083` |
| AsistenciaFeignClient | ms-asistencia | `http://ms-asistencia:8082` |
| ComunicacionFeignClient | ms-comunicaciones | `http://ms-comunicaciones:8081` |

## Ejecucion local

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

O desde Docker:

```bash
docker compose up --build ms-bff
```

## Notas de diseno

- Sin logica de negocio. Solo orquesta y mapea DTOs.
- Los headers `X-User-Uuid`, `X-User-Id` y `X-User-Role` los propaga el API Gateway y los reenvía `FeignInterceptorConfig` a los MS.
- La resolucion UUID → nombre de usuario se hace por peticion con cache en Map para evitar llamadas Feign repetidas.
