package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository;

import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio Spring Data JPA para AttendanceEntity.
 */
public interface AttendanceJpaRepository extends JpaRepository<AttendanceEntity, Long> {

    List<AttendanceEntity> findByStudentId(Long studentId);

    List<AttendanceEntity> findByStudentIdAndFecha(Long studentId, LocalDate fecha);
}
