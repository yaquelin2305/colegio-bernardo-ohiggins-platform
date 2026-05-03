package cl.duoc.colegio.academico.application.port.out;

import cl.duoc.colegio.academico.domain.model.Asignatura;
import java.util.List;
import java.util.Optional;

public interface AsignaturaRepositoryPort {
    Asignatura guardar(Asignatura asignatura);
    Optional<Asignatura> buscarPorId(Long id);
    List<Asignatura> listarTodas();
    boolean existePorId(Long id);
}
