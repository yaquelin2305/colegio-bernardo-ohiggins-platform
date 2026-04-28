package cl.duoc.colegio.usuario.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio Usuario.
 *
 * Representa el agregado raíz del dominio de autenticación.
 * NO tiene anotaciones de JPA ni de Spring — es PURO dominio.
 * La persistencia es responsabilidad de la capa de Infraestructura.
 */
public class Usuario {

    private UUID id;
    private String email;
    private String passwordHash;
    private RolUsuario rol;
    private String nombre;
    private String apellido;
    /** ID del perfil asociado según el rol (ej: id del estudiante, docente, etc.) */
    private Long perfilId;
    private boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructor para creación de nuevos usuarios
    public Usuario(String email, String passwordHash, RolUsuario rol, String nombre, String apellido) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = true;
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }

    // Constructor para reconstrucción desde persistencia
    public Usuario(UUID id, String email, String passwordHash, RolUsuario rol,
                   String nombre, String apellido, Long perfilId,
                   boolean activo, LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.perfilId = perfilId;
        this.activo = activo;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    // ── Reglas de Negocio del Dominio ──────────────────────────────────────────

    /**
     * Verifica si el usuario puede autenticarse (está activo).
     */
    public boolean puedeAutenticarse() {
        return this.activo;
    }

    /**
     * Desactiva el usuario (soft delete).
     */
    public void desactivar() {
        this.activo = false;
        this.actualizadoEn = LocalDateTime.now();
    }

    /**
     * Asocia un perfil específico al usuario (ej: id del estudiante en MS-Académico).
     */
    public void asociarPerfil(Long perfilId) {
        this.perfilId = perfilId;
        this.actualizadoEn = LocalDateTime.now();
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public RolUsuario getRol() { return rol; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public Long getPerfilId() { return perfilId; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
