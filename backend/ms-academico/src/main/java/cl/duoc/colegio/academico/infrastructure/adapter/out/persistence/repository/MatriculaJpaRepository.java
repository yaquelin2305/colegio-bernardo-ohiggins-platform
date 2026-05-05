package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository;

import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.MatriculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatriculaJpaRepository extends JpaRepository<MatriculaEntity, Long> {
    List<MatriculaEntity> findByCursoId(Long cursoId);
    List<MatriculaEntity> findByUsuarioUuid(UUID usuarioUuid);
    boolean existsByUsuarioUuidAndCursoId(UUID usuarioUuid, Long cursoId);
}
