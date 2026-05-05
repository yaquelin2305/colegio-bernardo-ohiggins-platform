-- ============================================================
-- MS-Usuario — Script de referencia (desarrollo local)
-- En docker-compose se usa docker/init.sql que consolida todo.
-- Ejecutar manualmente solo si levantás ms-usuario sin compose.
-- ============================================================

CREATE SCHEMA IF NOT EXISTS users_schema;

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

CREATE INDEX IF NOT EXISTS idx_usuarios_rut   ON users_schema.usuarios(rut);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON users_schema.usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_rol   ON users_schema.usuarios(rol);
CREATE INDEX IF NOT EXISTS idx_refresh_token  ON users_schema.refresh_tokens(token);

-- ✅ Usuario ADMIN inicial (password: Admin1234!)
-- Hash BCrypt strength=12 — cambiar en producción
INSERT INTO users_schema.usuarios (rut, email, password_hash, rol, nombre, apellido)
VALUES (
    '12345678-9',
    'admin@colegio-ohiggins.cl',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewFnpeMr8tSkqOGq',
    'ADMIN',
    'Administrador',
    'Sistema'
) ON CONFLICT (rut) DO NOTHING;
