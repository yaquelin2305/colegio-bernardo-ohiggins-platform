package cl.duoc.colegio.usuario.domain.exception;

public class UsuarioNoEncontradoException extends UsuarioDomainException {
    public UsuarioNoEncontradoException(String email) {
        super("Usuario no encontrado con email: " + email);
    }
}
