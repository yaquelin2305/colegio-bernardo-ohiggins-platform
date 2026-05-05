package cl.duoc.colegio.academico.domain.exception;

public class GradeNotFoundException extends AcademicoException {
    public GradeNotFoundException(Long id) {
        super("Nota no encontrada con id: " + id);
    }
}
