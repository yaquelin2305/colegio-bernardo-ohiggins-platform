package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository;

import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para GradeEntity.
 */
public interface GradeJpaRepository extends JpaRepository<GradeEntity, Long> {

    List<GradeEntity> findByStudentId(Long studentId);

    List<GradeEntity> findByStudentIdAndAsignatura(Long studentId, String asignatura);
}
