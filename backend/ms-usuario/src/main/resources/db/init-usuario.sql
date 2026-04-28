-- ============================================================
-- MS-Usuario — Inicialización de Base de Datos
-- Colegio Bernardo O'Higgins
-- ============================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    rol             VARCHAR(20)  NOT NULL CHECK (rol IN ('DOCENTE','APODERADO','ESTUDIANTE','ADMIN')),
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    perfil_id       BIGINT,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en       TIMESTAMP NOT NULL DEFAULT NOW(),
    actualizado_en  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_rol   ON usuarios(rol);

-- ── Usuario ADMIN inicial (password: Admin1234!)
-- Hash BCrypt generado con strength=12
-- Cambiar en producción inmediatamente
INSERT INTO usuarios (email, password_hash, rol, nombre, apellido)
VALUES (
    'admin@colegio-ohiggins.cl',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewFnpeMr8tSkqOGq',
    'ADMIN',
    'Administrador',
    'Sistema'
) ON CONFLICT (email) DO NOTHING;
