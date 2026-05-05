package cl.duoc.colegio.usuario.domain.exception;

public class RolNoSoportadoException extends UsuarioDomainException {
    public RolNoSoportadoException(String rol) {
        super("El rol '" + rol + "' no está soportado en el sistema.");
    }
}
