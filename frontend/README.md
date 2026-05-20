# Frontend — Colegio Bernardo O'Higgins

Aplicación web SPA para la plataforma de gestión escolar del Colegio Bernardo O'Higgins.

## Stack

- React 19 + Vite 8
- React Router DOM v7
- Axios v1
- lucide-react (iconos)

## Requisitos

- Node.js 20+
- El API Gateway corriendo en `http://localhost:8080`

## Desarrollo local

```bash
npm install
npm run dev
```

La app queda disponible en `http://localhost:5173`. Todas las peticiones API apuntan a `VITE_API_URL` (por defecto `http://localhost:8080`).

## Build de producción

```bash
npm run build
```

El resultado queda en `dist/` y es servido por Nginx dentro del contenedor Docker.

## Docker

```bash
# Desde la raíz del monorepo
docker compose up --build frontend
```

## Estructura

```
src/
├── core/           # Axios client, constantes, AuthContext
├── features/       # Módulos por dominio (auth, asistencia, comunicaciones, etc.)
├── shared/         # Componentes y utilidades reutilizables
└── assets/styles/  # Variables CSS globales
```

## Roles y acceso

| Rol | Acceso |
|---|---|
| ADMIN | Dashboard, gestión académica, usuarios, asignaturas, docentes |
| DOCENTE | Calificaciones, asistencia, comunicaciones |
| APODERADO | Boletin del pupilo, justificar inasistencias, comunicaciones |
| ESTUDIANTE | Sus propias calificaciones, historial asistencia, comunicaciones |
