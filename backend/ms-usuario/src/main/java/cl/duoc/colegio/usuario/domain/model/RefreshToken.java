package cl.duoc.colegio.usuario.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio: Refresh Token.
 *
 * Token opaco (UUID v4) que permite renovar el access token sin re-autenticar.
 * TTL: 7 días. Se invalida en logout o al ser usado (rotación one-time).
 */
public class RefreshToken {

    private final String token;          // UUID v4 opaco
    private final UUID usuarioId;
    private final LocalDateTime expiracion;
    private boolean revocado;

    public RefreshToken(String token, UUID usuarioId, LocalDateTime expiracion) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.expiracion = expiracion;
        this.revocado = false;
    }

    public boolean estaVigente() {
        return !revocado && LocalDateTime.now().isBefore(expiracion);
    }

    public void revocar() {
        this.revocado = true;
    }

    public String getToken()              { return token; }
    public UUID getUsuarioId()            { return usuarioId; }
    public LocalDateTime getExpiracion()  { return expiracion; }
    public boolean isRevocado()           { return revocado; }
}
