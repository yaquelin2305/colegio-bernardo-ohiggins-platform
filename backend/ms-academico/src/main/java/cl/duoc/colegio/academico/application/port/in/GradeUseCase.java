package cl.duoc.colegio.academico.application.port.in;

import cl.duoc.colegio.academico.domain.model.Grade;
import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada — Casos de uso de Notas.
 */
public interface GradeUseCase {

    Grade registrarNota(Grade grade);

    Grade obtenerNotaPorId(Long id);

    List<Grade> listarNotasPorEstudiante(UUID usuarioUuid);

    List<Grade> listarNotasPorEstudianteYAsignatura(UUID usuarioUuid, Long asignaturaId);

    double calcularPromedioEstudiante(UUID usuarioUuid);

    double calcularPromedioEstudiantePorAsignatura(UUID usuarioUuid, Long asignaturaId);

    Grade actualizarNota(Long id, Grade grade);

    void eliminarNota(Long id);
}
