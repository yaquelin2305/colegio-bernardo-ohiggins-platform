package cl.duoc.colegio.academico.application.port.out;

import cl.duoc.colegio.academico.domain.model.Curso;
import java.util.List;
import java.util.Optional;

public interface CursoRepositoryPort {
    Curso guardar(Curso curso);
    Optional<Curso> buscarPorId(Long id);
    List<Curso> listarTodos();
    boolean existePorId(Long id);
}
