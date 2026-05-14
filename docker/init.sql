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

CREATE INDEX IF NOT EXISTS idx_usuarios_rut    ON users_schema.usuarios(rut);
CREATE INDEX IF NOT EXISTS idx_usuarios_email  ON users_schema.usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_rol    ON users_schema.usuarios(rol);

-- ✅ Admin inicial — password: Admin1234!
INSERT INTO users_schema.usuarios (rut, email, password_hash, rol, nombre, apellido)
VALUES (
    '12345678-9',
    'admin@colegio-ohiggins.cl',
    '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey',
    'ADMIN',
    'Administrador',
    'Sistema'
) ON CONFLICT (rut) DO NOTHING;

-- ✅ 5 Docentes — password: Test1234!
INSERT INTO users_schema.usuarios (id, rut, email, password_hash, rol, nombre, apellido)
VALUES
    ('d0000001-0001-0001-0001-000000000001', '11111111-1', 'docente1@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'DOCENTE', 'Carlos',   'Muñoz'),
    ('d0000001-0001-0001-0001-000000000002', '11111111-2', 'docente2@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'DOCENTE', 'Camila',   'Rojas'),
    ('d0000001-0001-0001-0001-000000000003', '11111111-3', 'docente3@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'DOCENTE', 'Felipe',   'Soto'),
    ('d0000001-0001-0001-0001-000000000004', '11111111-4', 'docente4@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'DOCENTE', 'Valeria',  'Díaz'),
    ('d0000001-0001-0001-0001-000000000005', '11111111-5', 'docente5@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'DOCENTE', 'Patricio', 'Silva')
ON CONFLICT (rut) DO NOTHING;

-- ✅ 5 Estudiantes — password: Test1234!
INSERT INTO users_schema.usuarios (id, rut, email, password_hash, rol, nombre, apellido)
VALUES
    ('e0000001-0001-0001-0001-000000000001', '22222222-1', 'estudiante1@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'ESTUDIANTE', 'Martín',    'Araya'),
    ('e0000001-0001-0001-0001-000000000002', '22222222-2', 'estudiante2@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'ESTUDIANTE', 'Sofía',     'Carrasco'),
    ('e0000001-0001-0001-0001-000000000003', '22222222-3', 'estudiante3@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'ESTUDIANTE', 'Benjamín',  'Tapia'),
    ('e0000001-0001-0001-0001-000000000004', '22222222-4', 'estudiante4@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'ESTUDIANTE', 'Isidora',   'Fuentes'),
    ('e0000001-0001-0001-0001-000000000005', '22222222-5', 'estudiante5@cbo.cl', '$2a$12$7kz1/ao1uApsSLjCzWvtC.FLZslb33tuFIZFJ.9DYrndiPfxWpuee', 'ESTUDIANTE', 'Diego',     'Morales')
ON CONFLICT (rut) DO NOTHING;

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

-- ══════════════════════════════════════════════════════════
-- SEED DATA — 5 registros por tabla para pruebas GET
-- ══════════════════════════════════════════════════════════

-- ✅ 5 Cursos
INSERT INTO academico_schema.cursos (id, nombre, anio_escolar, profesor_jefe_uuid)
VALUES
    (1, '1° Básico A',   2026, 'd0000001-0001-0001-0001-000000000001'),
    (2, '2° Básico A',   2026, 'd0000001-0001-0001-0001-000000000002'),
    (3, '3° Básico A',   2026, 'd0000001-0001-0001-0001-000000000003'),
    (4, '4° Básico A',   2026, 'd0000001-0001-0001-0001-000000000004'),
    (5, '5° Básico A',   2026, 'd0000001-0001-0001-0001-000000000005')
ON CONFLICT (id) DO NOTHING;

-- ✅ 5 Asignaturas
INSERT INTO academico_schema.asignaturas (id, nombre, horas_semanales)
VALUES
    (1, 'Matemáticas',       6),
    (2, 'Lenguaje',          6),
    (3, 'Ciencias Naturales', 4),
    (4, 'Historia',           4),
    (5, 'Inglés',             3)
ON CONFLICT (id) DO NOTHING;

-- ✅ 5 Matrículas (cada estudiante en un curso distinto)
INSERT INTO academico_schema.matriculas (id, usuario_uuid, curso_id)
VALUES
    (1, 'e0000001-0001-0001-0001-000000000001', 1),
    (2, 'e0000001-0001-0001-0001-000000000002', 2),
    (3, 'e0000001-0001-0001-0001-000000000003', 3),
    (4, 'e0000001-0001-0001-0001-000000000004', 4),
    (5, 'e0000001-0001-0001-0001-000000000005', 5)
ON CONFLICT (id) DO NOTHING;

-- ✅ 5 Asignaciones Docente (cada docente con una asignatura en su curso)
INSERT INTO academico_schema.asignacion_docentes (id, docente_uuid, curso_id, asignatura_id)
VALUES
    (1, 'd0000001-0001-0001-0001-000000000001', 1, 1),
    (2, 'd0000001-0001-0001-0001-000000000002', 2, 2),
    (3, 'd0000001-0001-0001-0001-000000000003', 3, 3),
    (4, 'd0000001-0001-0001-0001-000000000004', 4, 4),
    (5, 'd0000001-0001-0001-0001-000000000005', 5, 5)
ON CONFLICT (id) DO NOTHING;

-- ✅ 5 Calificaciones (cada estudiante con notas en 1 asignatura)
INSERT INTO academico_schema.calificaciones (id, usuario_uuid, asignatura_id, nota_1, nota_2, nota_3, promedio)
VALUES
    (1, 'e0000001-0001-0001-0001-000000000001', 1, 6.5, 5.8, 7.0, 6.4),
    (2, 'e0000001-0001-0001-0001-000000000002', 2, 5.0, 6.2, 5.5, 5.6),
    (3, 'e0000001-0001-0001-0001-000000000003', 3, 7.0, 6.8, 7.0, 6.9),
    (4, 'e0000001-0001-0001-0001-000000000004', 4, 4.5, 5.0, 4.8, 4.8),
    (5, 'e0000001-0001-0001-0001-000000000005', 5, 6.0, 6.5, 5.5, 6.0)
ON CONFLICT (id) DO NOTHING;

-- ✅ 5 Students (registro por estudiante en tabla students)
INSERT INTO academico_schema.students (id, usuario_uuid)
VALUES
    (1, 'e0000001-0001-0001-0001-000000000001'),
    (2, 'e0000001-0001-0001-0001-000000000002'),
    (3, 'e0000001-0001-0001-0001-000000000003'),
    (4, 'e0000001-0001-0001-0001-000000000004'),
    (5, 'e0000001-0001-0001-0001-000000000005')
ON CONFLICT (usuario_uuid) DO NOTHING;

-- ✅ 5 Asistencias (1 por estudiante, fechas mayo 2026)
INSERT INTO academico_schema.attendances (id, usuario_uuid, fecha, presente)
VALUES
    (1, 'e0000001-0001-0001-0001-000000000001', '2026-05-11', true),
    (2, 'e0000001-0001-0001-0001-000000000002', '2026-05-12', true),
    (3, 'e0000001-0001-0001-0001-000000000003', '2026-05-13', false),
    (4, 'e0000001-0001-0001-0001-000000000004', '2026-05-11', true),
    (5, 'e0000001-0001-0001-0001-000000000005', '2026-05-12', true)
ON CONFLICT (id) DO NOTHING;

-- ✅ Resetear secuencias (post-INSERT con IDs explícitos)
SELECT setval('academico_schema.cursos_id_seq',              COALESCE((SELECT MAX(id) FROM academico_schema.cursos), 1));
SELECT setval('academico_schema.asignaturas_id_seq',         COALESCE((SELECT MAX(id) FROM academico_schema.asignaturas), 1));
SELECT setval('academico_schema.matriculas_id_seq',          COALESCE((SELECT MAX(id) FROM academico_schema.matriculas), 1));
SELECT setval('academico_schema.asignacion_docentes_id_seq', COALESCE((SELECT MAX(id) FROM academico_schema.asignacion_docentes), 1));
SELECT setval('academico_schema.calificaciones_id_seq',      COALESCE((SELECT MAX(id) FROM academico_schema.calificaciones), 1));
SELECT setval('academico_schema.students_id_seq',            COALESCE((SELECT MAX(id) FROM academico_schema.students), 1));
SELECT setval('academico_schema.attendances_id_seq',         COALESCE((SELECT MAX(id) FROM academico_schema.attendances), 1));
