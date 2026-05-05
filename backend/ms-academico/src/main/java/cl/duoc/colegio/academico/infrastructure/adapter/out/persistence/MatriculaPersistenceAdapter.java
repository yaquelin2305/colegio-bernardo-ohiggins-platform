package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.application.port.out.MatriculaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Matricula;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.MatriculaEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.MatriculaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MatriculaPersistenceAdapter implements MatriculaRepositoryPort {

    private final MatriculaJpaRepository jpaRepository;

    public MatriculaPersistenceAdapter(MatriculaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Matricula guardar(Matricula matricula) {
        return toDomain(jpaRepository.save(toEntity(matricula)));
    }

    @Override
    public Optional<Matricula> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Matricula> buscarPorCursoId(Long cursoId) {
        return jpaRepository.findByCursoId(cursoId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Matricula> buscarPorUsuarioUuid(UUID usuarioUuid) {
        return jpaRepository.findByUsuarioUuid(usuarioUuid).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorUsuarioUuidYCursoId(UUID usuarioUuid, Long cursoId) {
        return jpaRepository.existsByUsuarioUuidAndCursoId(usuarioUuid, cursoId);
    }

    private Matricula toDomain(MatriculaEntity e) {
        return new Matricula(e.getId(), e.getUsuarioUuid(), e.getCursoId());
    }

    private MatriculaEntity toEntity(Matricula m) {
        return MatriculaEntity.builder()
                .id(m.getId())
                .usuarioUuid(m.getUsuarioUuid())
                .cursoId(m.getCursoId())
                .build();
    }
}
