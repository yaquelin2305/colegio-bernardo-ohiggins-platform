-- ============================================================
-- INIT.SQL — Plataforma Colegio Bernardo O'Higgins
-- Una sola BD (colegio_db) con schemas separados por MS:
--   users_schema    → MS-Usuario
--   academico_schema → MS-Academico
-- ============================================================

-- ── SCHEMAS ────────────────────────────────────────────────
CREATE SCHEMA IF NOT EXISTS users_schema;
CREATE SCHEMA IF NOT EXISTS academico_schema;

-- ══════════════════════════════════════════════════════════
-- USERS_SCHEMA — MS-Usuario
-- ══════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS users_schema.usuarios (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    rut             VARCHAR(12)  NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    rol             VARCHAR(20)  NOT NULL CHECK (rol IN ('DOCENTE','APODERADO','ESTUDIANTE','ADMIN')),
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    perfil_id       BIGINT,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en       TIMESTAMP    NOT NULL DEFAULT NOW(),
    actualizado_en  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS users_schema.refresh_tokens (
    id          BIGSERIAL    PRIMARY KEY,
    token       VARCHAR(255) NOT NULL UNIQUE,
    usuario_id  UUID         NOT NULL REFERENCES users_schema.usuarios(id) ON DELETE CASCADE,
    expira_en   TIMESTAMP    NOT NULL,
    revocado    BOOLEAN      NOT NULL DEFAULT FALSE,
    creado_en   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_usuarios_rut    ON users_schema.usuarios(rut);
CREATE INDEX IF NOT EXISTS idx_usuarios_email  ON users_schema.usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_rol    ON users_schema.usuarios(rol);
CREATE INDEX IF NOT EXISTS idx_refresh_token   ON users_schema.refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_usuario ON users_schema.refresh_tokens(usuario_id);

-- ✅ Admin inicial — password: Admin1234!
INSERT INTO users_schema.usuarios (rut, email, password_hash, rol, nombre, apellido)
VALUES (
    '12345678-9',
    'admin@colegio-ohiggins.cl',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewFnpeMr8tSkqOGq',
    'ADMIN',
    'Administrador',
    'Sistema'
) ON CONFLICT (rut) DO NOTHING;

-- ══════════════════════════════════════════════════════════
-- ACADEMICO_SCHEMA — MS-Academico
-- ══════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS academico_schema.cursos (
    id                 BIGSERIAL    PRIMARY KEY,
    nombre             VARCHAR(100) NOT NULL,
    anio_escolar       INTEGER      NOT NULL,
    profesor_jefe_uuid UUID
);

CREATE TABLE IF NOT EXISTS academico_schema.asignaturas (
    id              BIGSERIAL    PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    horas_semanales INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS academico_schema.matriculas (
    id           BIGSERIAL PRIMARY KEY,
    usuario_uuid UUID      NOT NULL,
    curso_id     BIGINT    NOT NULL REFERENCES academico_schema.cursos(id),
    CONSTRAINT uq_matricula UNIQUE (usuario_uuid, curso_id)
);

CREATE TABLE IF NOT EXISTS academico_schema.asignacion_docentes (
    id            BIGSERIAL PRIMARY KEY,
    docente_uuid  UUID      NOT NULL,
    curso_id      BIGINT    NOT NULL REFERENCES academico_schema.cursos(id),
    asignatura_id BIGINT    NOT NULL REFERENCES academico_schema.asignaturas(id)
);

CREATE TABLE IF NOT EXISTS academico_schema.calificaciones (
    id            BIGSERIAL        PRIMARY KEY,
    usuario_uuid  UUID             NOT NULL,
    asignatura_id BIGINT           NOT NULL REFERENCES academico_schema.asignaturas(id),
    nota_1        DOUBLE PRECISION,
    nota_2        DOUBLE PRECISION,
    nota_3        DOUBLE PRECISION,
    promedio      DOUBLE PRECISION NOT NULL DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS academico_schema.students (
    id           BIGSERIAL PRIMARY KEY,
    usuario_uuid UUID      NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS academico_schema.attendances (
    id           BIGSERIAL PRIMARY KEY,
    usuario_uuid UUID      NOT NULL,
    fecha        DATE      NOT NULL,
    presente     BOOLEAN   NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_matriculas_curso    ON academico_schema.matriculas(curso_id);
CREATE INDEX IF NOT EXISTS idx_matriculas_usuario  ON academico_schema.matriculas(usuario_uuid);
CREATE INDEX IF NOT EXISTS idx_calif_uuid          ON academico_schema.calificaciones(usuario_uuid);
CREATE INDEX IF NOT EXISTS idx_calif_asig          ON academico_schema.calificaciones(asignatura_id);
