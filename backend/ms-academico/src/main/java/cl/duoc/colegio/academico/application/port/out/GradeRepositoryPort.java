package cl.duoc.colegio.academico.application.port.out;

import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.domain.model.GradeContract;
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

    Optional<GradeContract> buscarContratoPorUsuarioUuidYAsignaturaId(UUID usuarioUuid, Long asignaturaId);

    GradeContract guardarContrato(GradeContract contrato);

    List<GradeContract> buscarContratosPorUsuarioUuid(UUID usuarioUuid);
}
