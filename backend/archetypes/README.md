# Arquetipos Maven — Colegio Bernardo O'Higgins

> Plantillas reutilizables para generar nuevos microservicios backend

## Arquetipos disponibles

### 1. `archetype-hexagonal` — Base para microservicios genéricos

**Incluye:**
- Arquitectura Hexagonal (Ports & Adapters)
- Spring Boot 3.2.5 + JPA + Lombok
- JaCoCo con umbral mínimo 60% cobertura
- Ejemplo funcional: `Ejemplo` (CRUD completo)
- Dockerfile multi-stage

### 2. `archetype-usuario` — Base para microservicios de autenticación

**Incluye:**
- Todo lo del arquetipo hexagonal
- Spring Security (trust-the-gateway)
- JWT con jjwt 0.11.5
- BCrypt (strength 12)
- Patrón Strategy para autorización por roles
- Patrón Factory Method
- Login por RUT chileno

---

## Cómo usar los arquetipos

### Paso 1: Instalar el arquetipo en el repositorio local

```bash
cd backend/archetypes/archetype-hexagonal
mvn clean install

cd backend/archetypes/archetype-usuario
mvn clean install
```

### Paso 2: Generar un nuevo proyecto

```bash
# Crear directorio para el nuevo microservicio
mkdir nuevo-ms && cd nuevo-ms

# Generar desde arquetipo hexagonal
mvn archetype:generate \
  -DarchetypeGroupId=cl.duoc.colegio \
  -DarchetypeArtifactId=archetype-hexagonal \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=cl.duoc.colegio \
  -DartifactId=ms-asistencia \
  -Dversion=1.0.0 \
  -Dpackage=cl.duoc.colegio.msasistencia \
  -Ddescripcion="Microservicio de Asistencia"

# O desde arquetipo de usuario/auth
mvn archetype:generate \
  -DarchetypeGroupId=cl.duoc.colegio \
  -DarchetypeArtifactId=archetype-usuario \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=cl.duoc.colegio \
  -DartifactId=ms-nuevo-auth \
  -Dversion=1.0.0
```

### Paso 3: Compilar y ejecutar

```bash
cd ms-asistencia
mvn clean package -DskipTests
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Parámetros personalizables

| Parámetro | Descripción | Ejemplo |
|---|---|---|
| `groupId` | Grupo Maven del proyecto | `cl.duoc.colegio` |
| `artifactId` | Nombre del microservicio | `ms-asistencia` |
| `version` | Versión inicial | `1.0.0` |
| `package` | Paquete Java base | `cl.duoc.colegio.msasistencia` |
| `descripcion` | Descripción del proyecto (solo hexagonal) | `Microservicio de Asistencia` |

---

## Patrones de diseño en los arquetipos

| Patrón | Arquetipo | Propósito |
|---|---|---|
| Hexagonal (Ports & Adapters) | Ambos | Separación dominio ↔ infraestructura |
| Repository | Ambos | Abstracción de persistencia |
| Factory Method | Ambos | Creación centralizada de objetos |
| Strategy | usuario | Autorización por rol sin if/else |
| DTO | Ambos | Transporte inmutable de datos |
| Singleton (Spring Beans) | Ambos | Instancia única gestionada por IoC |

---

*Equipo: Yaquelin Rugel · Yeider Catari · Victor Barrera · María José Velázquez · Eliezer Carrasco*
*Docente: Alexis Jacob Jiménez Parada — Desarrollo Fullstack III*
