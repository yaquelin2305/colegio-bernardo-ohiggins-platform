-- ============================================================
-- INIT.SQL — Plataforma Colegio Bernardo O'Higgins
-- Una sola BD (colegio_db) con schemas separados por MS:
--   users_schema          → MS-Usuario
--   academico_schema      → MS-Academico
--   asistencia_schema     → MS-Asistencia
--   comunicaciones_schema → MS-Comunicaciones
-- ============================================================

-- ── SCHEMAS ────────────────────────────────────────────────
CREATE SCHEMA IF NOT EXISTS users_schema;
CREATE SCHEMA IF NOT EXISTS academico_schema;
CREATE SCHEMA IF NOT EXISTS asistencia_schema;
CREATE SCHEMA IF NOT EXISTS comunicaciones_schema;

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
    pupilo_uuid     UUID,
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

-- MS-Asistencia: tabla gestionada por Hibernate ddl-auto:update, definida aquí para consistencia
CREATE TABLE IF NOT EXISTS asistencia_schema.asistencias (
    id            BIGSERIAL PRIMARY KEY,
    estudiante_id VARCHAR(255),
    curso_id      VARCHAR(255),
    fecha         DATE,
    estado        VARCHAR(50),
    observacion   TEXT
);

CREATE INDEX IF NOT EXISTS idx_asistencias_estudiante ON asistencia_schema.asistencias(estudiante_id);
CREATE INDEX IF NOT EXISTS idx_asistencias_curso      ON asistencia_schema.asistencias(curso_id);
CREATE INDEX IF NOT EXISTS idx_asistencias_fecha      ON asistencia_schema.asistencias(fecha);

-- MS-Comunicaciones: tabla gestionada por Hibernate ddl-auto:update, definida aquí para consistencia
CREATE TABLE IF NOT EXISTS comunicaciones_schema.comunicaciones (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  VARCHAR(255),
    destinatario VARCHAR(255),
    asunto      VARCHAR(255),
    mensaje     TEXT,
    tipo        VARCHAR(100),
    canal       VARCHAR(20),
    fecha_envio TIMESTAMP,
    leido       BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_comunicaciones_destinatario ON comunicaciones_schema.comunicaciones(destinatario);
CREATE INDEX IF NOT EXISTS idx_comunicaciones_usuario      ON comunicaciones_schema.comunicaciones(usuario_id);

-- ══════════════════════════════════════════════════════════
-- SEED DATA — Para prueba end-to-end de los 4 roles
-- Password para TODOS los usuarios sembrados: Admin1234!
-- Hash bcrypt reutilizado del admin.
-- ══════════════════════════════════════════════════════════

-- ── DOCENTES ──
INSERT INTO users_schema.usuarios (id, rut, email, password_hash, rol, nombre, apellido) VALUES
  ('11111111-1111-1111-1111-111111111101', '11111111-1', 'docente.juan@colegio-ohiggins.cl',  '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'DOCENTE', 'Juan',  'Pérez'),
  ('11111111-1111-1111-1111-111111111102', '22222222-2', 'docente.marta@colegio-ohiggins.cl', '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'DOCENTE', 'Marta', 'Gómez')
ON CONFLICT (rut) DO NOTHING;

-- ── ESTUDIANTES ──
INSERT INTO users_schema.usuarios (id, rut, email, password_hash, rol, nombre, apellido) VALUES
  ('22222222-2222-2222-2222-222222222201', '30000001-1', 'estudiante.diego@colegio-ohiggins.cl',  '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'ESTUDIANTE', 'Diego',  'Soto'),
  ('22222222-2222-2222-2222-222222222202', '30000002-2', 'estudiante.ana@colegio-ohiggins.cl',    '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'ESTUDIANTE', 'Ana',    'Reyes'),
  ('22222222-2222-2222-2222-222222222203', '30000003-3', 'estudiante.luis@colegio-ohiggins.cl',   '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'ESTUDIANTE', 'Luis',   'Torres'),
  ('22222222-2222-2222-2222-222222222204', '30000004-4', 'estudiante.camila@colegio-ohiggins.cl', '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'ESTUDIANTE', 'Camila', 'Vidal')
ON CONFLICT (rut) DO NOTHING;

-- ── APODERADOS (cada uno vinculado a un estudiante vía pupilo_uuid) ──
INSERT INTO users_schema.usuarios (id, rut, email, password_hash, rol, nombre, apellido, pupilo_uuid) VALUES
  ('33333333-3333-3333-3333-333333333301', '40000001-1', 'apoderado.pedro@colegio-ohiggins.cl',  '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'APODERADO', 'Pedro',  'Soto',   '22222222-2222-2222-2222-222222222201'),
  ('33333333-3333-3333-3333-333333333302', '40000002-2', 'apoderado.laura@colegio-ohiggins.cl',  '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'APODERADO', 'Laura',  'Reyes',  '22222222-2222-2222-2222-222222222202'),
  ('33333333-3333-3333-3333-333333333303', '40000003-3', 'apoderado.carlos@colegio-ohiggins.cl', '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'APODERADO', 'Carlos', 'Torres', '22222222-2222-2222-2222-222222222203'),
  ('33333333-3333-3333-3333-333333333304', '40000004-4', 'apoderado.sofia@colegio-ohiggins.cl',  '$2a$12$0IsFtHBXJGnszHB/rF/Q3e2sKpcpM2Y/mKM88SM3REX5riGPyv4ey', 'APODERADO', 'Sofía',  'Vidal',  '22222222-2222-2222-2222-222222222204')
ON CONFLICT (rut) DO NOTHING;

-- ── CURSOS ──
INSERT INTO academico_schema.cursos (id, nombre, anio_escolar, profesor_jefe_uuid) VALUES
  (1, '1°A 2026', 2026, '11111111-1111-1111-1111-111111111101'),
  (2, '2°B 2026', 2026, '11111111-1111-1111-1111-111111111102')
ON CONFLICT (id) DO NOTHING;
SELECT setval('academico_schema.cursos_id_seq', GREATEST((SELECT MAX(id) FROM academico_schema.cursos), 1));

-- ── ASIGNATURAS ──
INSERT INTO academico_schema.asignaturas (id, nombre, horas_semanales) VALUES
  (1, 'Matemática', 6),
  (2, 'Lenguaje',   6),
  (3, 'Historia',   4)
ON CONFLICT (id) DO NOTHING;
SELECT setval('academico_schema.asignaturas_id_seq', GREATEST((SELECT MAX(id) FROM academico_schema.asignaturas), 1));

-- ── MATRÍCULAS (4 estudiantes en curso 1) ──
INSERT INTO academico_schema.matriculas (usuario_uuid, curso_id) VALUES
  ('22222222-2222-2222-2222-222222222201', 1),
  ('22222222-2222-2222-2222-222222222202', 1),
  ('22222222-2222-2222-2222-222222222203', 1),
  ('22222222-2222-2222-2222-222222222204', 1)
ON CONFLICT (usuario_uuid, curso_id) DO NOTHING;

-- ── ASIGNACIONES DOCENTES ──
INSERT INTO academico_schema.asignacion_docentes (docente_uuid, curso_id, asignatura_id)
SELECT '11111111-1111-1111-1111-111111111101', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM academico_schema.asignacion_docentes WHERE docente_uuid='11111111-1111-1111-1111-111111111101' AND curso_id=1 AND asignatura_id=1);
INSERT INTO academico_schema.asignacion_docentes (docente_uuid, curso_id, asignatura_id)
SELECT '11111111-1111-1111-1111-111111111101', 1, 3
WHERE NOT EXISTS (SELECT 1 FROM academico_schema.asignacion_docentes WHERE docente_uuid='11111111-1111-1111-1111-111111111101' AND curso_id=1 AND asignatura_id=3);
INSERT INTO academico_schema.asignacion_docentes (docente_uuid, curso_id, asignatura_id)
SELECT '11111111-1111-1111-1111-111111111102', 1, 2
WHERE NOT EXISTS (SELECT 1 FROM academico_schema.asignacion_docentes WHERE docente_uuid='11111111-1111-1111-1111-111111111102' AND curso_id=1 AND asignatura_id=2);

-- ── CALIFICACIONES (1 fila por estudiante × asignatura del curso 1) ──
INSERT INTO academico_schema.calificaciones (usuario_uuid, asignatura_id, nota_1, nota_2, nota_3, promedio)
SELECT v.usuario_uuid, v.asignatura_id, v.n1, v.n2, v.n3, ROUND(((v.n1+v.n2+v.n3)/3.0)::numeric, 1)
FROM (VALUES
  ('22222222-2222-2222-2222-222222222201'::uuid, 1::bigint, 6.0, 5.5, 6.2),
  ('22222222-2222-2222-2222-222222222201'::uuid, 2::bigint, 5.8, 6.0, 6.5),
  ('22222222-2222-2222-2222-222222222201'::uuid, 3::bigint, 6.5, 6.0, 5.7),
  ('22222222-2222-2222-2222-222222222202'::uuid, 1::bigint, 6.8, 6.5, 6.7),
  ('22222222-2222-2222-2222-222222222202'::uuid, 2::bigint, 6.2, 6.0, 6.4),
  ('22222222-2222-2222-2222-222222222202'::uuid, 3::bigint, 5.9, 6.1, 6.3),
  ('22222222-2222-2222-2222-222222222203'::uuid, 1::bigint, 5.5, 5.8, 6.0),
  ('22222222-2222-2222-2222-222222222203'::uuid, 2::bigint, 6.0, 5.7, 5.9),
  ('22222222-2222-2222-2222-222222222203'::uuid, 3::bigint, 5.8, 6.2, 5.6),
  ('22222222-2222-2222-2222-222222222204'::uuid, 1::bigint, 6.3, 6.6, 6.8),
  ('22222222-2222-2222-2222-222222222204'::uuid, 2::bigint, 6.5, 6.7, 6.4),
  ('22222222-2222-2222-2222-222222222204'::uuid, 3::bigint, 6.0, 6.2, 6.1)
) AS v(usuario_uuid, asignatura_id, n1, n2, n3)
WHERE NOT EXISTS (
  SELECT 1 FROM academico_schema.calificaciones c
  WHERE c.usuario_uuid = v.usuario_uuid AND c.asignatura_id = v.asignatura_id
);

-- ── ASISTENCIAS (3 fechas por estudiante: 2 PRESENTE + 1 AUSENTE) ──
INSERT INTO asistencia_schema.asistencias (estudiante_id, curso_id, fecha, estado, observacion)
SELECT v.estudiante_id, '1', v.fecha::date, v.estado, v.obs
FROM (VALUES
  ('22222222-2222-2222-2222-222222222201', '2026-05-12', 'PRESENTE', NULL),
  ('22222222-2222-2222-2222-222222222201', '2026-05-13', 'AUSENTE',  NULL),
  ('22222222-2222-2222-2222-222222222201', '2026-05-14', 'PRESENTE', NULL),
  ('22222222-2222-2222-2222-222222222202', '2026-05-12', 'PRESENTE', NULL),
  ('22222222-2222-2222-2222-222222222202', '2026-05-13', 'PRESENTE', NULL),
  ('22222222-2222-2222-2222-222222222202', '2026-05-14', 'AUSENTE',  NULL),
  ('22222222-2222-2222-2222-222222222203', '2026-05-12', 'AUSENTE',  NULL),
  ('22222222-2222-2222-2222-222222222203', '2026-05-13', 'PRESENTE', NULL),
  ('22222222-2222-2222-2222-222222222203', '2026-05-14', 'PRESENTE', NULL),
  ('22222222-2222-2222-2222-222222222204', '2026-05-12', 'PRESENTE', NULL),
  ('22222222-2222-2222-2222-222222222204', '2026-05-13', 'PRESENTE', NULL),
  ('22222222-2222-2222-2222-222222222204', '2026-05-14', 'AUSENTE',  NULL)
) AS v(estudiante_id, fecha, estado, obs)
WHERE NOT EXISTS (
  SELECT 1 FROM asistencia_schema.asistencias a
  WHERE a.estudiante_id = v.estudiante_id AND a.fecha = v.fecha::date
);

-- ── COMUNICACIONES (mensajes de bienvenida) ──
INSERT INTO comunicaciones_schema.comunicaciones (usuario_id, destinatario, asunto, mensaje, tipo, canal, fecha_envio, leido)
SELECT v.usuario_id, v.destinatario, v.asunto, v.mensaje, v.tipo, v.canal, v.fecha_envio::timestamp, false
FROM (VALUES
  ('00000000-0000-0000-0000-000000000000', '33333333-3333-3333-3333-333333333301', 'Bienvenida al sistema',     'Estimado apoderado, le damos la bienvenida a la plataforma del Colegio Bernardo O''Higgins.', 'INFORMATIVO', 'EMAIL', '2026-05-10 09:00:00'),
  ('11111111-1111-1111-1111-111111111101', '33333333-3333-3333-3333-333333333301', 'Reunión de apoderados',     'Se le invita a la reunión de apoderados del curso 1°A el próximo viernes.',                  'CITACION',    'EMAIL', '2026-05-11 10:30:00'),
  ('11111111-1111-1111-1111-111111111102', '33333333-3333-3333-3333-333333333302', 'Avance académico',          'Su pupila Ana ha mostrado excelente desempeño este mes.',                                    'INFORMATIVO', 'EMAIL', '2026-05-12 11:15:00')
) AS v(usuario_id, destinatario, asunto, mensaje, tipo, canal, fecha_envio)
WHERE NOT EXISTS (
  SELECT 1 FROM comunicaciones_schema.comunicaciones c
  WHERE c.usuario_id = v.usuario_id AND c.destinatario = v.destinatario AND c.asunto = v.asunto
);
