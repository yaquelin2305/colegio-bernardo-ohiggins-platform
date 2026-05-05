package cl.duoc.colegio.usuario.domain.model;

/**
 * Enumeración de roles del sistema.
 *
 * DOCENTE    → Acceso a cursos asignados, puede ver y modificar notas y asistencias.
 * APODERADO  → Acceso SOLO a datos del pupilo asociado (lectura).
 * ESTUDIANTE → Acceso SOLO a sus propios datos (lectura).
 * ADMIN      → Acceso total al sistema (gestión de usuarios y configuración).
 */
public enum RolUsuario {
    DOCENTE,
    APODERADO,
    ESTUDIANTE,
    ADMIN
}
