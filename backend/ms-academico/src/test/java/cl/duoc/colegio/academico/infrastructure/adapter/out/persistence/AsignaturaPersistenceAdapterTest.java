package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.domain.model.Asignatura;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AsignaturaEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.AsignaturaJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AsignaturaPersistenceAdapter — Pruebas Unitarias")
class AsignaturaPersistenceAdapterTest {

    @Mock
    private AsignaturaJpaRepository jpaRepository;

    @InjectMocks
    private AsignaturaPersistenceAdapter adapter;

    private AsignaturaEntity entity() {
        return AsignaturaEntity.builder().id(1L).nombre("Matemáticas").horasSemanales(6).build();
    }

    @Test
    void guardar_persisteYRetorna() {
        when(jpaRepository.save(any(AsignaturaEntity.class))).thenReturn(entity());
        Asignatura result = adapter.guardar(new Asignatura(null, "Matemáticas", 6));
        assertThat(result.getNombre()).isEqualTo("Matemáticas");
    }

    @Test
    void buscarPorId_existe_retornaAsignatura() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity()));
        assertThat(adapter.buscarPorId(1L)).isPresent();
    }

    @Test
    void buscarPorId_noExiste_retornaEmpty() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(adapter.buscarPorId(99L)).isEmpty();
    }

    @Test
    void listarTodas_retornaLista() {
        when(jpaRepository.findAll()).thenReturn(List.of(entity()));
        assertThat(adapter.listarTodas()).hasSize(1);
    }

    @Test
    void existePorId_retornaTrue() {
        when(jpaRepository.existsById(1L)).thenReturn(true);
        assertThat(adapter.existePorId(1L)).isTrue();
    }

    @Test
    void existePorId_retornaFalse() {
        when(jpaRepository.existsById(99L)).thenReturn(false);
        assertThat(adapter.existePorId(99L)).isFalse();
    }
}
