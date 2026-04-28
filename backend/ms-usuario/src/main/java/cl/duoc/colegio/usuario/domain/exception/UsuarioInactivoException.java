package cl.duoc.colegio.usuario.domain.exception;

public class UsuarioInactivoException extends UsuarioDomainException {
    public UsuarioInactivoException(String email) {
        super("La cuenta del usuario '" + email + "' está inactiva. Contacte al administrador.");
    }
}
