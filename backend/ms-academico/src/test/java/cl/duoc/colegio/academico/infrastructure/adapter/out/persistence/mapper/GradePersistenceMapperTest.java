package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper;

import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.GradeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GradePersistenceMapper — Pruebas Unitarias")
class GradePersistenceMapperTest {

    private final GradePersistenceMapper mapper = new GradePersistenceMapper();

    @Test
    void toDomain_mapeaNota1ComoNota() {
        UUID uuid = UUID.randomUUID();
        GradeEntity entity = GradeEntity.builder()
                .id(1L).usuarioUuid(uuid).asignaturaId(5L)
                .nota1(6.5).nota2(5.0).nota3(4.0).promedio(5.17)
                .build();

        Grade domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getNota()).isEqualTo(6.5);
        assertThat(domain.getTipo()).isEqualTo("REGISTRADA");
    }

    @Test
    void toEntity_mapeaNotaANota1() {
        UUID uuid = UUID.randomUUID();
        Grade domain = new Grade(1L, uuid, 5L, 6.0, "PRUEBA", "Test");

        GradeEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNota1()).isEqualTo(6.0);
        assertThat(entity.getNota2()).isNull();
        assertThat(entity.getNota3()).isNull();
        assertThat(entity.getPromedio()).isEqualTo(6.0);
    }
}
