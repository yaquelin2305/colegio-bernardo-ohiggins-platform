package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper;

import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.GradeEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper manual: Grade (dominio) <-> GradeEntity (JPA).
 */
@Component
public class GradePersistenceMapper {

    public Grade toDomain(GradeEntity entity) {
        return new Grade(
                entity.getId(),
                entity.getStudentId(),
                entity.getAsignatura(),
                entity.getNota(),
                entity.getTipo(),
                entity.getFecha(),
                entity.getDescripcion()
        );
    }

    public GradeEntity toEntity(Grade domain) {
        return GradeEntity.builder()
                .id(domain.getId())
                .studentId(domain.getStudentId())
                .asignatura(domain.getAsignatura())
                .nota(domain.getNota())
                .tipo(domain.getTipo())
                .fecha(domain.getFecha())
                .descripcion(domain.getDescripcion())
                .build();
    }
}
