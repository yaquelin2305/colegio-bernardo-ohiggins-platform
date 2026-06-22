package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper;

import cl.duoc.colegio.academico.domain.model.Attendance;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AttendanceEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AttendancePersistenceMapper — Pruebas Unitarias")
class AttendancePersistenceMapperTest {

    private final AttendancePersistenceMapper mapper = new AttendancePersistenceMapper();

    @Test
    void toDomain_presente_mapeaCorrecto() {
        LocalDate fecha = LocalDate.of(2026, 6, 1);
        AttendanceEntity entity = AttendanceEntity.builder()
                .id(1L).studentId(100L).asignatura("Matemáticas")
                .fecha(fecha).presente(true).justificacion(null)
                .build();

        Attendance domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getStudentId()).isEqualTo(100L);
        assertThat(domain.isPresente()).isTrue();
        assertThat(domain.getJustificacion()).isNull();
    }

    @Test
    void toDomain_ausenteConJustificacion_mapeaCorrecto() {
        LocalDate fecha = LocalDate.of(2026, 6, 2);
        AttendanceEntity entity = AttendanceEntity.builder()
                .id(2L).studentId(100L).asignatura("Lenguaje")
                .fecha(fecha).presente(false).justificacion("Enfermedad")
                .build();

        Attendance domain = mapper.toDomain(entity);

        assertThat(domain.isPresente()).isFalse();
        assertThat(domain.getJustificacion()).isEqualTo("Enfermedad");
    }

    @Test
    void toEntity_mapeaCorrecto() {
        LocalDate fecha = LocalDate.of(2026, 6, 1);
        Attendance domain = new Attendance(1L, 100L, "Matemáticas", fecha, true, null);

        AttendanceEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getPresente()).isTrue();
        assertThat(entity.getJustificacion()).isNull();
    }
}
