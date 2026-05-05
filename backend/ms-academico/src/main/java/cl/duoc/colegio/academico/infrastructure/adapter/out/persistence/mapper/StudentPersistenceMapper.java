package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper;

import cl.duoc.colegio.academico.domain.model.Student;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.StudentEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper manual: Student (dominio) <-> StudentEntity (JPA).
 * Mantiene el desacoplamiento — el dominio no conoce JPA.
 */
@Component
public class StudentPersistenceMapper {

    public Student toDomain(StudentEntity entity) {
        return new Student(
                entity.getId(),
                entity.getRut(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getCurso()
        );
    }

    public StudentEntity toEntity(Student domain) {
        return StudentEntity.builder()
                .id(domain.getId())
                .rut(domain.getRut())
                .nombre(domain.getNombre())
                .apellido(domain.getApellido())
                .curso(domain.getCurso())
                .build();
    }
}
