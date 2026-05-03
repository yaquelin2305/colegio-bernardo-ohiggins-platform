package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository;

import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para GradeEntity (calificaciones).
 */
public interface GradeJpaRepository extends JpaRepository<GradeEntity, Long> {

    List<GradeEntity> findByUsuarioUuid(UUID usuarioUuid);

    Optional<GradeEntity> findByUsuarioUuidAndAsignaturaId(UUID usuarioUuid, Long asignaturaId);
}
