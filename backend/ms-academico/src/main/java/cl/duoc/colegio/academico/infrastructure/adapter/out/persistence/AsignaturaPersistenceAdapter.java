package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.application.port.out.AsignaturaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Asignatura;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AsignaturaEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.AsignaturaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AsignaturaPersistenceAdapter implements AsignaturaRepositoryPort {

    private final AsignaturaJpaRepository jpaRepository;

    public AsignaturaPersistenceAdapter(AsignaturaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Asignatura guardar(Asignatura asignatura) {
        return toDomain(jpaRepository.save(toEntity(asignatura)));
    }

    @Override
    public Optional<Asignatura> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Asignatura> listarTodas() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }

    private Asignatura toDomain(AsignaturaEntity e) {
        return new Asignatura(e.getId(), e.getNombre(), e.getHorasSemanales());
    }

    private AsignaturaEntity toEntity(Asignatura a) {
        return AsignaturaEntity.builder()
                .id(a.getId())
                .nombre(a.getNombre())
                .horasSemanales(a.getHorasSemanales())
                .build();
    }
}
