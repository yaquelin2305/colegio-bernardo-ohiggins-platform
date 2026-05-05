package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.application.port.out.AsignacionDocenteRepositoryPort;
import cl.duoc.colegio.academico.domain.model.AsignacionDocente;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AsignacionDocenteEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.AsignacionDocenteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AsignacionDocentePersistenceAdapter implements AsignacionDocenteRepositoryPort {

    private final AsignacionDocenteJpaRepository jpaRepository;

    public AsignacionDocentePersistenceAdapter(AsignacionDocenteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AsignacionDocente guardar(AsignacionDocente asignacion) {
        return toDomain(jpaRepository.save(toEntity(asignacion)));
    }

    @Override
    public Optional<AsignacionDocente> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AsignacionDocente> buscarPorDocenteUuid(UUID docenteUuid) {
        return jpaRepository.findByDocenteUuid(docenteUuid).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorDocenteCursoAsignatura(UUID docenteUuid, Long cursoId, Long asignaturaId) {
        return jpaRepository.existsByDocenteUuidAndCursoIdAndAsignaturaId(docenteUuid, cursoId, asignaturaId);
    }

    private AsignacionDocente toDomain(AsignacionDocenteEntity e) {
        return new AsignacionDocente(e.getId(), e.getDocenteUuid(), e.getCursoId(), e.getAsignaturaId());
    }

    private AsignacionDocenteEntity toEntity(AsignacionDocente a) {
        return AsignacionDocenteEntity.builder()
                .id(a.getId())
                .docenteUuid(a.getDocenteUuid())
                .cursoId(a.getCursoId())
                .asignaturaId(a.getAsignaturaId())
                .build();
    }
}
