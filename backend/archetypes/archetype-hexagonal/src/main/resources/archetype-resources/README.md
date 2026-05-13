# ${artifactId}

> ${descripcion} — Colegio Bernardo O'Higgins

Microservicio generado con el arquetipo **Hexagonal (Ports & Adapters)**.

## Estructura

```
src/main/java/
├── Application.java
├── domain/
│   ├── model/            ← Entidades puras (sin JPA)
│   ├── port/
│   │   ├── in/           ← Puertos de entrada (UseCase)
│   │   └── out/          ← Puertos de salida (RepositoryPort)
│   └── exception/        ← Excepciones de dominio
├── application/
│   ├── usecase/          ← Implementaciones de casos de uso
│   └── dto/              ← DTOs de respuesta
└── infrastructure/
    ├── adapter/
    │   ├── in/rest/      ← Controladores REST
    │   └── out/persistence/ ← JPA Entities, Repos, Adapters
    └── config/           ← GlobalExceptionHandler
```

## Ejecución

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar (dev)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Tests con cobertura
mvn verify
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/v1/ejemplos` | Listar todos |
| `GET` | `/api/v1/ejemplos/{id}` | Obtener por ID |
| `POST` | `/api/v1/ejemplos` | Crear nuevo |

## Docker

```bash
docker build -t ${artifactId}:${version} .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod ${artifactId}:${version}
```
