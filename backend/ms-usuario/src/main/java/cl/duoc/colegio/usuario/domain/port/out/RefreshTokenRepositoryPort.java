package cl.duoc.colegio.usuario.domain.port.out;

import cl.duoc.colegio.usuario.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida: persistencia de refresh tokens.
 */
public interface RefreshTokenRepositoryPort {
    RefreshToken guardar(RefreshToken refreshToken);
    Optional<RefreshToken> buscarVigente(String token);
    void revocarTodosPorUsuario(UUID usuarioId);
}
