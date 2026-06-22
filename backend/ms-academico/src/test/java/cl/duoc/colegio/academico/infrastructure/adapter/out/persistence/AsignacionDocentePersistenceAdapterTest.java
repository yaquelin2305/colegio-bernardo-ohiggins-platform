package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.domain.model.AsignacionDocente;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AsignacionDocenteEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.AsignacionDocenteJpaRepository;
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
@DisplayName("AsignacionDocentePersistenceAdapter — Pruebas Unitarias")
class AsignacionDocentePersistenceAdapterTest {

    @Mock
    private AsignacionDocenteJpaRepository jpaRepository;

    @InjectMocks
    private AsignacionDocentePersistenceAdapter adapter;

    private UUID uuid = UUID.randomUUID();
    private AsignacionDocenteEntity entity = AsignacionDocenteEntity.builder()
            .id(1L).docenteUuid(uuid).cursoId(10L).asignaturaId(5L).build();

    @Test
    void guardar_persisteYRetorna() {
        when(jpaRepository.save(any())).thenReturn(entity);
        AsignacionDocente result = adapter.guardar(new AsignacionDocente(null, uuid, 10L, 5L));
        assertThat(result.getDocenteUuid()).isEqualTo(uuid);
    }

    @Test
    void buscarPorId_existe_retorna() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        assertThat(adapter.buscarPorId(1L)).isPresent();
    }

    @Test
    void buscarPorId_noExiste_retornaEmpty() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(adapter.buscarPorId(99L)).isEmpty();
    }

    @Test
    void buscarPorDocenteUuid_retornaLista() {
        when(jpaRepository.findByDocenteUuid(uuid)).thenReturn(List.of(entity));
        assertThat(adapter.buscarPorDocenteUuid(uuid)).hasSize(1);
    }

    @Test
    void buscarTodas_retornaLista() {
        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        assertThat(adapter.buscarTodas()).hasSize(1);
    }

    @Test
    void existePorDocenteCursoAsignatura() {
        when(jpaRepository.existsByDocenteUuidAndCursoIdAndAsignaturaId(uuid, 10L, 5L)).thenReturn(true);
        assertThat(adapter.existePorDocenteCursoAsignatura(uuid, 10L, 5L)).isTrue();
    }

    @Test
    void eliminar_llamaDeleteById() {
        adapter.eliminar(1L);
        verify(jpaRepository).deleteById(1L);
    }
}
