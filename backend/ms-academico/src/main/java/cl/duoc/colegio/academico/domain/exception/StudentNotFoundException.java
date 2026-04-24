package cl.duoc.colegio.academico.domain.exception;

public class StudentNotFoundException extends AcademicoException {
    public StudentNotFoundException(Long id) {
        super("Estudiante no encontrado con id: " + id);
    }
    public StudentNotFoundException(String rut) {
        super("Estudiante no encontrado con RUT: " + rut);
    }
}
