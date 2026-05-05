package cl.duoc.colegio.usuario.domain.exception;

/**
 * Excepción de dominio base del MS-Usuario.
 */
public class UsuarioDomainException extends RuntimeException {
    public UsuarioDomainException(String message) {
        super(message);
    }
    public UsuarioDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
