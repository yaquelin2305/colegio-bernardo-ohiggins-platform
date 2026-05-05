package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository;

import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AsignacionDocenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AsignacionDocenteJpaRepository extends JpaRepository<AsignacionDocenteEntity, Long> {
    List<AsignacionDocenteEntity> findByDocenteUuid(UUID docenteUuid);
    boolean existsByDocenteUuidAndCursoIdAndAsignaturaId(UUID docenteUuid, Long cursoId, Long asignaturaId);
}
