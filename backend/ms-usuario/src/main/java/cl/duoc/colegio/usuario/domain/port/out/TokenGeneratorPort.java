package cl.duoc.colegio.usuario.domain.port.out;

import cl.duoc.colegio.usuario.domain.model.Usuario;

/**
 * Puerto de salida para la generación de tokens JWT.
 *
 * Desacopla el dominio de la implementación concreta de JWT.
 */
public interface TokenGeneratorPort {
    String generarToken(Usuario usuario);
    boolean validarToken(String token);
    String extraerEmail(String token);
}
