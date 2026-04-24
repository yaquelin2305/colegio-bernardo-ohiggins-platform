-- =============================================================
-- INIT-DB.SQL — Inicialización de la BD para Docker Compose
-- Se ejecuta automáticamente la primera vez que arranca postgres
-- =============================================================

-- Tabla: estudiantes
CREATE TABLE IF NOT EXISTS estudiantes (
    id       BIGSERIAL    PRIMARY KEY,
    rut      VARCHAR(12)  NOT NULL UNIQUE,
    nombre   VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    curso    INTEGER      NOT NULL
);

-- Tabla: notas
CREATE TABLE IF NOT EXISTS notas (
    id          BIGSERIAL        PRIMARY KEY,
    student_id  BIGINT           NOT NULL,
    asignatura  VARCHAR(100)     NOT NULL,
    nota        DOUBLE PRECISION NOT NULL,
    tipo        VARCHAR(50)      NOT NULL,
    fecha       DATE             NOT NULL,
    descripcion VARCHAR(255)
);

-- Tabla: asistencias
CREATE TABLE IF NOT EXISTS asistencias (
    id            BIGSERIAL    PRIMARY KEY,
    student_id    BIGINT       NOT NULL,
    asignatura    VARCHAR(100) NOT NULL,
    fecha         DATE         NOT NULL,
    presente      BOOLEAN      NOT NULL,
    justificacion VARCHAR(500)
);
