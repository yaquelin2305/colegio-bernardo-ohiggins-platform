package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.domain.model.Matricula;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.MatriculaEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.MatriculaJpaRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatriculaPersistenceAdapter — Pruebas Unitarias")
class MatriculaPersistenceAdapterTest {

    @Mock
    private MatriculaJpaRepository jpaRepository;

    @InjectMocks
    private MatriculaPersistenceAdapter adapter;

    private UUID uuid = UUID.randomUUID();
    private MatriculaEntity entity = MatriculaEntity.builder().id(1L).usuarioUuid(uuid).cursoId(10L).build();

    @Test
    void guardar_persisteYRetorna() {
        when(jpaRepository.save(any(MatriculaEntity.class))).thenReturn(entity);
        Matricula result = adapter.guardar(new Matricula(null, uuid, 10L));
        assertThat(result.getUsuarioUuid()).isEqualTo(uuid);
    }

    @Test
    void buscarPorId_existe_retornaMatricula() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        assertThat(adapter.buscarPorId(1L)).isPresent();
    }

    @Test
    void buscarPorId_noExiste_retornaEmpty() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(adapter.buscarPorId(99L)).isEmpty();
    }

    @Test
    void buscarPorCursoId_retornaLista() {
        when(jpaRepository.findByCursoId(10L)).thenReturn(List.of(entity));
        assertThat(adapter.buscarPorCursoId(10L)).hasSize(1);
    }

    @Test
    void buscarPorUsuarioUuid_retornaLista() {
        when(jpaRepository.findByUsuarioUuid(uuid)).thenReturn(List.of(entity));
        assertThat(adapter.buscarPorUsuarioUuid(uuid)).hasSize(1);
    }

    @Test
    void buscarTodas_retornaLista() {
        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        assertThat(adapter.buscarTodas()).hasSize(1);
    }

    @Test
    void existePorUsuarioUuidYCursoId_retornaTrue() {
        when(jpaRepository.existsByUsuarioUuidAndCursoId(uuid, 10L)).thenReturn(true);
        assertThat(adapter.existePorUsuarioUuidYCursoId(uuid, 10L)).isTrue();
    }
}
