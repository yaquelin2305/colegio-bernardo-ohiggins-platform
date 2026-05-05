package cl.duoc.colegio.academico.application.port.out;

import cl.duoc.colegio.academico.domain.model.Matricula;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatriculaRepositoryPort {
    Matricula guardar(Matricula matricula);
    Optional<Matricula> buscarPorId(Long id);
    List<Matricula> buscarPorCursoId(Long cursoId);
    List<Matricula> buscarPorUsuarioUuid(UUID usuarioUuid);
    boolean existePorUsuarioUuidYCursoId(UUID usuarioUuid, Long cursoId);
}
