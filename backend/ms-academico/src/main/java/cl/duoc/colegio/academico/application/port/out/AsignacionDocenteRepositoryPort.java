package cl.duoc.colegio.academico.application.port.out;

import cl.duoc.colegio.academico.domain.model.AsignacionDocente;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AsignacionDocenteRepositoryPort {
    AsignacionDocente guardar(AsignacionDocente asignacion);
    Optional<AsignacionDocente> buscarPorId(Long id);
    List<AsignacionDocente> buscarPorDocenteUuid(UUID docenteUuid);
    boolean existePorDocenteCursoAsignatura(UUID docenteUuid, Long cursoId, Long asignaturaId);
}
