package cl.duoc.colegio.usuario.domain.exception;

public class EmailYaRegistradoException extends UsuarioDomainException {
    public EmailYaRegistradoException(String email) {
        super("El email '" + email + "' ya está registrado en el sistema.");
    }
}
