package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.application.port.out.CursoRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Curso;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.CursoEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.CursoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CursoPersistenceAdapter implements CursoRepositoryPort {

    private final CursoJpaRepository jpaRepository;

    public CursoPersistenceAdapter(CursoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Curso guardar(Curso curso) {
        return toDomain(jpaRepository.save(toEntity(curso)));
    }

    @Override
    public Optional<Curso> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Curso> listarTodos() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }

    private Curso toDomain(CursoEntity e) {
        return new Curso(e.getId(), e.getNombre(), e.getAnioEscolar(), e.getProfesorJefeUuid());
    }

    private CursoEntity toEntity(Curso c) {
        return CursoEntity.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .anioEscolar(c.getAnioEscolar())
                .profesorJefeUuid(c.getProfesorJefeUuid())
                .build();
    }
}
