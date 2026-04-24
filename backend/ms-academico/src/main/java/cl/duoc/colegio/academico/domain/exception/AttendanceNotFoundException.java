package cl.duoc.colegio.academico.domain.exception;

public class AttendanceNotFoundException extends AcademicoException {
    public AttendanceNotFoundException(Long id) {
        super("Registro de asistencia no encontrado con id: " + id);
    }
}
