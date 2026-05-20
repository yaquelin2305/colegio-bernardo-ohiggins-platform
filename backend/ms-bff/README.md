# ms-bff - Backend For Frontend

Microservicio orquestador que recibe peticiones del frontend y las resuelve llamando a los microservicios internos via OpenFeign.

## Tecnologias

- Java 17 + Spring Boot 3
- Spring Cloud OpenFeign
- Eureka Client
- Lombok

## Puerto

8084

## Endpoints principales

| Ruta | Descripcion |
|---|---|
| GET /api/bff/dashboard/stats | estadisticas generales |
| GET /api/bff/cursos | listado de cursos |
| GET /api/bff/asignaturas | listado de asignaturas |
| GET /api/bff/usuarios/{rol} | usuarios por rol |
| GET /api/bff/boletin/{id} | boletin de un estudiante |
| GET /api/bff/asistencia/** | endpoints de asistencia |
| GET /api/bff/comunicaciones/** | endpoints de mensajeria |

## Levantar

```bash
docker compose up --build ms-bff
```

## Notas

No tiene logica de negocio. Solo orquesta llamadas y transforma DTOs.
Los headers X-User-Uuid, X-User-Id y X-User-Role los propaga el gateway y el BFF los reenvía a los microservicios.
