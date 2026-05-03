package cl.duoc.colegio.usuario.domain.port.out;

import cl.duoc.colegio.usuario.domain.model.Usuario;

/**
 * Puerto de salida para la generación de tokens JWT.
 * Desacopla el dominio de la implementación concreta.
 */
public interface TokenGeneratorPort {
    /** Genera el access token (24h). El sub es el RUT del usuario. */
    String generarToken(Usuario usuario);

    /** Genera el refresh token opaco (7 días). Persiste en BD. */
    String generarRefreshToken(Usuario usuario);

    boolean validarToken(String token);

    /** Extrae el RUT (sub) del token. */
    String extraerRut(String token);
}
