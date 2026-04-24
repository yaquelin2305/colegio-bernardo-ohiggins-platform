package cl.duoc.colegio.academico.domain.exception;

/**
 * Excepción base del dominio académico.
 */
public class AcademicoException extends RuntimeException {
    public AcademicoException(String message) {
        super(message);
    }
    public AcademicoException(String message, Throwable cause) {
        super(message, cause);
    }
}
