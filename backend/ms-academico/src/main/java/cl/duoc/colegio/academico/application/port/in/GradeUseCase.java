package cl.duoc.colegio.academico.application.port.in;

import cl.duoc.colegio.academico.domain.model.Grade;
import java.util.List;

/**
 * Puerto de entrada — Casos de uso de Notas.
 */
public interface GradeUseCase {

    Grade registrarNota(Grade grade);

    Grade obtenerNotaPorId(Long id);

    List<Grade> listarNotasPorEstudiante(Long studentId);

    List<Grade> listarNotasPorEstudianteYAsignatura(Long studentId, String asignatura);

    double calcularPromedioEstudiante(Long studentId);

    double calcularPromedioEstudiantePorAsignatura(Long studentId, String asignatura);

    Grade actualizarNota(Long id, Grade grade);

    void eliminarNota(Long id);
}
