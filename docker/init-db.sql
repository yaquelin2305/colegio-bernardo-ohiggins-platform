-- ============================================================
-- MS-Academico — Inicialización de Base de Datos
-- Schema: academico_schema
-- ============================================================

CREATE SCHEMA IF NOT EXISTS academico_schema;

CREATE TABLE IF NOT EXISTS academico_schema.cursos (
    id              BIGSERIAL    PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    anio_escolar    INTEGER      NOT NULL,
    profesor_jefe_uuid UUID
);

CREATE TABLE IF NOT EXISTS academico_schema.asignaturas (
    id              BIGSERIAL    PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    horas_semanales INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS academico_schema.matriculas (
    id              BIGSERIAL    PRIMARY KEY,
    usuario_uuid    UUID         NOT NULL,
    curso_id        BIGINT       NOT NULL REFERENCES academico_schema.cursos(id),
    CONSTRAINT uq_matricula UNIQUE (usuario_uuid, curso_id)
);

CREATE TABLE IF NOT EXISTS academico_schema.asignacion_docentes (
    id              BIGSERIAL    PRIMARY KEY,
    docente_uuid    UUID         NOT NULL,
    curso_id        BIGINT       NOT NULL REFERENCES academico_schema.cursos(id),
    asignatura_id   BIGINT       NOT NULL REFERENCES academico_schema.asignaturas(id)
);

CREATE TABLE IF NOT EXISTS academico_schema.calificaciones (
    id              BIGSERIAL        PRIMARY KEY,
    usuario_uuid    UUID             NOT NULL,
    asignatura_id   BIGINT           NOT NULL REFERENCES academico_schema.asignaturas(id),
    nota_1          DOUBLE PRECISION,
    nota_2          DOUBLE PRECISION,
    nota_3          DOUBLE PRECISION,
    promedio        DOUBLE PRECISION NOT NULL DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS academico_schema.students (
    id              BIGSERIAL    PRIMARY KEY,
    usuario_uuid    UUID         NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS academico_schema.attendances (
    id              BIGSERIAL    PRIMARY KEY,
    usuario_uuid    UUID         NOT NULL,
    fecha           DATE         NOT NULL,
    presente        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_matriculas_curso     ON academico_schema.matriculas(curso_id);
CREATE INDEX IF NOT EXISTS idx_matriculas_usuario   ON academico_schema.matriculas(usuario_uuid);
CREATE INDEX IF NOT EXISTS idx_calificaciones_uuid  ON academico_schema.calificaciones(usuario_uuid);
CREATE INDEX IF NOT EXISTS idx_calificaciones_asig  ON academico_schema.calificaciones(asignatura_id);
