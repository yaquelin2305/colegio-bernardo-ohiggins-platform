package cl.duoc.colegio.academico.application.port.out;

import cl.duoc.colegio.academico.domain.model.Grade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida — Repositorio de Notas.
 */
public interface GradeRepositoryPort {

    Grade guardar(Grade grade);

    Optional<Grade> buscarPorId(Long id);

    List<Grade> buscarPorUsuarioUuid(UUID usuarioUuid);

    List<Grade> buscarPorUsuarioUuidYAsignaturaId(UUID usuarioUuid, Long asignaturaId);

    void eliminar(Long id);
}
