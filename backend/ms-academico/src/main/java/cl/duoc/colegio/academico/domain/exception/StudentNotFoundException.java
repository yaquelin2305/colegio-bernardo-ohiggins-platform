package cl.duoc.colegio.academico.domain.exception;

import java.util.UUID;

public class StudentNotFoundException extends AcademicoException {
    public StudentNotFoundException(Long id) {
        super("Estudiante no encontrado con id: " + id);
    }
    public StudentNotFoundException(UUID usuarioUuid) {
        super("Estudiante no encontrado con usuarioUuid: " + usuarioUuid);
    }
    public StudentNotFoundException(String rut) {
        super("Estudiante no encontrado con RUT: " + rut);
    }
}
