package cl.duoc.colegio.usuario.domain.exception;

public class CredencialesInvalidasException extends UsuarioDomainException {
    public CredencialesInvalidasException() {
        super("Credenciales inválidas. Verifique su RUT y contraseña.");
    }
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
