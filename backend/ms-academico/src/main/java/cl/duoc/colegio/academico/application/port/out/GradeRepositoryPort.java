package cl.duoc.colegio.academico.application.port.out;

import cl.duoc.colegio.academico.domain.model.Grade;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida — Repositorio de Notas.
 */
public interface GradeRepositoryPort {

    Grade guardar(Grade grade);

    Optional<Grade> buscarPorId(Long id);

    List<Grade> buscarPorStudentId(Long studentId);

    List<Grade> buscarPorStudentIdYAsignatura(Long studentId, String asignatura);

    void eliminar(Long id);
}
