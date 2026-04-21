# 🎓 Plataforma Educativa Colegio Bernardo O’Higgins

Sistema web moderno basado en **Microservicios + Arquitectura Hexagonal**, diseñado para digitalizar la gestión académica, asistencia y comunicación escolar del Colegio Bernardo O’Higgins de Coquimbo.

## 📌 Descripción del Proyecto

Este proyecto nace como solución a los problemas administrativos y académicos actuales del establecimiento, tales como:

- Dependencia de libro de clases físico.
- Dificultad para revisar historial académico.
- Procesos lentos para reportes institucionales.
- Comunicación deficiente entre docentes, alumnos y apoderados.
- Falta de herramientas digitales modernas.

La plataforma permite una administración centralizada, segura y escalable.

---

## 🏗️ Arquitectura del Sistema

El sistema fue diseñado utilizando:

### 🔹 Microservicios
Cada módulo funciona de forma independiente.

### 🔹 Arquitectura Hexagonal
Separación entre lógica de negocio e infraestructura.

### 🔹 API Gateway
Punto único de entrada y seguridad del sistema.

### 🔹 BFF (Backend For Frontend)
Orquestador que centraliza consultas del frontend.

---

## ⚙️ Microservicios Principales

### 👤 MS-USERS
Gestión de usuarios, roles y autenticación JWT.

Roles disponibles:

- Administrador
- Docente
- Apoderado

---

### 📚 MS-1 Gestión Académica

Permite:

- Registro de notas
- Asignaturas
- Promedios
- Historial académico

---

### 📝 MS-2 Asistencia

Permite:

- Registro diario de asistencia
- Justificaciones
- Reportes de asistencia

---

### 📩 MS-3 Comunicaciones

Permite:

- Mensajería oficial
- Notificaciones
- Alertas a apoderados

---

## 💻 Stack Tecnológico

### Backend

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Cloud Gateway
- OpenFeign

### Frontend

- React
- Vite
- JavaScript

### Base de Datos

- PostgreSQL

### DevOps / Cloud

- Docker
- Railway
- Netlify

### Testing / Calidad

- JUnit 5
- Mockito
- SonarQube

---

## 🔐 Seguridad

La plataforma utiliza:

- Autenticación JWT
- Control por roles
- API Gateway protegido
- Acceso restringido por perfil

---

## 🌱 Green IT / Sostenibilidad

La solución fue diseñada para optimizar recursos cloud:

- Escalado solo cuando se necesite.
- Menor consumo de CPU y memoria.
- Reducción de costos operativos.
- Menor huella de carbono digital.

---

## 🚀 Beneficios del Sistema

✅ Elimina procesos manuales  
✅ Mejora la comunicación escolar  
✅ Reportes rápidos y automáticos  
✅ Sistema escalable y moderno  
✅ Mayor seguridad de datos  
✅ Mejor experiencia para usuarios  

---

## 👨‍💻 Integrantes

- Yaquelin Rugel
- Yeider Catari
- Victor Barrera
- Maria José Velazques
- Eliezer Carrasco

---

## 📚 Asignatura

**Desarrollo Fullstack III**  
Docente: Alexis Jacob Jimenez Parada

---

## 📌 Estado del Proyecto


📍 Preparado para Desarrollo e Implementación

---

## ⭐ Tecnologías Clave

`Java` `Spring Boot` `React` `JWT` `Docker` `Railway` `PostgreSQL`

---
