package cl.duoc.colegio.usuario.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para la tabla refresh_tokens.
 * Schema: users_schema
 */
@Entity
@Table(name = "refresh_tokens", schema = "users_schema")
public class RefreshTokenEntity {

    @Id
    @Column(length = 36, nullable = false, unique = true)
    private String token;

    @Column(name = "usuario_id", nullable = false, columnDefinition = "uuid")
    private UUID usuarioId;

    @Column(nullable = false)
    private LocalDateTime expiracion;

    @Column(nullable = false)
    private boolean revocado = false;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() { this.creadoEn = LocalDateTime.now(); }

    // Getters y setters
    public String getToken()                          { return token; }
    public void setToken(String token)                { this.token = token; }
    public UUID getUsuarioId()                        { return usuarioId; }
    public void setUsuarioId(UUID usuarioId)          { this.usuarioId = usuarioId; }
    public LocalDateTime getExpiracion()              { return expiracion; }
    public void setExpiracion(LocalDateTime exp)      { this.expiracion = exp; }
    public boolean isRevocado()                       { return revocado; }
    public void setRevocado(boolean revocado)         { this.revocado = revocado; }
    public LocalDateTime getCreadoEn()                { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn)  { this.creadoEn = creadoEn; }
}
