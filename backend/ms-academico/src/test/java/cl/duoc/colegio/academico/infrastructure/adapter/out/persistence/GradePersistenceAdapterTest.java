package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.domain.model.GradeContract;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.GradeEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper.GradePersistenceMapper;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.GradeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GradePersistenceAdapter — Pruebas Unitarias")
class GradePersistenceAdapterTest {

    @Mock
    private GradeJpaRepository jpaRepository;

    @Mock
    private GradePersistenceMapper mapper;

    @InjectMocks
    private GradePersistenceAdapter adapter;

    private UUID uuid = UUID.randomUUID();
    private GradeEntity entity = GradeEntity.builder()
            .id(1L).usuarioUuid(uuid).asignaturaId(5L)
            .nota1(6.0).nota2(5.5).nota3(5.0).promedio(5.5)
            .build();
    private Grade domain = new Grade(1L, uuid, 5L, 6.0, "REGISTRADA", null);

    @Test
    void guardar_persisteYRetorna() {
        when(mapper.toEntity(any())).thenReturn(entity);
        when(jpaRepository.save(any())).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Grade result = adapter.guardar(domain);
        assertThat(result.getNota()).isEqualTo(6.0);
    }

    @Test
    void buscarPorId_existe_retorna() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.buscarPorId(1L)).isPresent();
    }

    @Test
    void buscarPorId_noExiste_retornaEmpty() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(adapter.buscarPorId(99L)).isEmpty();
    }

    @Test
    void buscarPorUsuarioUuid_retornaLista() {
        when(jpaRepository.findByUsuarioUuid(uuid)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.buscarPorUsuarioUuid(uuid)).hasSize(1);
    }

    @Test
    void buscarPorUsuarioUuidYAsignaturaId_retornaLista() {
        when(jpaRepository.findByUsuarioUuidAndAsignaturaId(uuid, 5L)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.buscarPorUsuarioUuidYAsignaturaId(uuid, 5L)).hasSize(1);
    }

    @Test
    void eliminar_llamaDeleteById() {
        adapter.eliminar(1L);
        verify(jpaRepository).deleteById(1L);
    }

    @Test
    void buscarContratoPorUsuarioUuidYAsignaturaId_existe_retorna() {
        when(jpaRepository.findByUsuarioUuidAndAsignaturaId(uuid, 5L)).thenReturn(List.of(entity));

        Optional<GradeContract> result = adapter.buscarContratoPorUsuarioUuidYAsignaturaId(uuid, 5L);

        assertThat(result).isPresent();
        assertThat(result.get().getNota1()).isEqualTo(6.0);
        assertThat(result.get().getPromedio()).isEqualTo(5.5);
    }

    @Test
    void buscarContratoPorUsuarioUuidYAsignaturaId_noExiste_retornaEmpty() {
        when(jpaRepository.findByUsuarioUuidAndAsignaturaId(uuid, 5L)).thenReturn(List.of());

        assertThat(adapter.buscarContratoPorUsuarioUuidYAsignaturaId(uuid, 5L)).isEmpty();
    }

    @Test
    void guardarContrato_nuevo_persiste() {
        GradeContract contrato = new GradeContract(uuid, 5L, 6.0, 5.5, 5.0, 5.5);
        when(jpaRepository.findByUsuarioUuidAndAsignaturaId(uuid, 5L)).thenReturn(List.of());
        when(jpaRepository.save(any(GradeEntity.class))).thenReturn(entity);

        GradeContract result = adapter.guardarContrato(contrato);

        assertThat(result.getPromedio()).isEqualTo(5.5);
    }

    @Test
    void guardarContrato_existente_actualiza() {
        GradeContract contrato = new GradeContract(uuid, 5L, 7.0, 6.0, 5.0, 6.0);
        when(jpaRepository.findByUsuarioUuidAndAsignaturaId(uuid, 5L)).thenReturn(List.of(entity));
        when(jpaRepository.save(any(GradeEntity.class))).thenReturn(entity);

        GradeContract result = adapter.guardarContrato(contrato);

        assertThat(result).isNotNull();
    }

    @Test
    void buscarContratosPorUsuarioUuid_retornaLista() {
        when(jpaRepository.findByUsuarioUuid(uuid)).thenReturn(List.of(entity));

        List<GradeContract> result = adapter.buscarContratosPorUsuarioUuid(uuid);

        assertThat(result).hasSize(1);
    }
}
