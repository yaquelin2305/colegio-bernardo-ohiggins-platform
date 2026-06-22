package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper;

import cl.duoc.colegio.academico.domain.model.Student;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.StudentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StudentPersistenceMapper — Pruebas Unitarias")
class StudentPersistenceMapperTest {

    private final StudentPersistenceMapper mapper = new StudentPersistenceMapper();

    @Test
    void toDomain_mapeaCorrectamente() {
        StudentEntity entity = StudentEntity.builder()
                .id(1L).rut("11111111-1").nombre("Juan").apellido("Pérez").curso(3).build();

        Student domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getRut()).isEqualTo("11111111-1");
        assertThat(domain.getNombre()).isEqualTo("Juan");
        assertThat(domain.getApellido()).isEqualTo("Pérez");
        assertThat(domain.getCurso()).isEqualTo(3);
    }

    @Test
    void toEntity_mapeaCorrectamente() {
        Student domain = new Student(1L, "11111111-1", "Juan", "Pérez", 3);

        StudentEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getRut()).isEqualTo("11111111-1");
        assertThat(entity.getNombre()).isEqualTo("Juan");
        assertThat(entity.getApellido()).isEqualTo("Pérez");
        assertThat(entity.getCurso()).isEqualTo(3);
    }
}
