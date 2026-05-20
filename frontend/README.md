# Frontend - Colegio Bernardo O'Higgins

Aplicacion SPA desarrollada con React 19 y Vite. Consume la API del gateway en el puerto 8080.

## Tecnologias

- React 19 + Vite
- React Router DOM v7
- Axios
- lucide-react

## Levantar en desarrollo

```bash
npm install
npm run dev
```

Requiere el gateway corriendo en `http://localhost:8080`.

## Build y Docker

```bash
# Solo el contenedor del frontend
docker compose up --build frontend

# Stack completo
docker compose up --build
```

## Estructura de carpetas

```
src/
  core/        configuracion global, axios, contexto de auth
  features/    modulos por funcionalidad (auth, asistencia, comunicaciones, etc)
  shared/      componentes y utilidades reutilizables
```

## Roles

- ADMIN: dashboard, gestion academica, usuarios
- DOCENTE: calificaciones, asistencia, comunicaciones
- APODERADO: boletin del pupilo, justificaciones, comunicaciones
- ESTUDIANTE: sus notas, asistencia, comunicaciones
