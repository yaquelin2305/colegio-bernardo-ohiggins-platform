package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper;

import cl.duoc.colegio.academico.domain.model.Attendance;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AttendanceEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper manual: Attendance (dominio) <-> AttendanceEntity (JPA).
 */
@Component
public class AttendancePersistenceMapper {

    public Attendance toDomain(AttendanceEntity entity) {
        return new Attendance(
                entity.getId(),
                entity.getStudentId(),
                entity.getAsignatura(),
                entity.getFecha(),
                entity.getPresente(),
                entity.getJustificacion()
        );
    }

    public AttendanceEntity toEntity(Attendance domain) {
        return AttendanceEntity.builder()
                .id(domain.getId())
                .studentId(domain.getStudentId())
                .asignatura(domain.getAsignatura())
                .fecha(domain.getFecha())
                .presente(domain.isPresente())
                .justificacion(domain.getJustificacion())
                .build();
    }
}
