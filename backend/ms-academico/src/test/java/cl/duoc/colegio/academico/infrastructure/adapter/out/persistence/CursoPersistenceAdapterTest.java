package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.domain.model.Curso;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.CursoEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.CursoJpaRepository;
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
@DisplayName("CursoPersistenceAdapter — Pruebas Unitarias")
class CursoPersistenceAdapterTest {

    @Mock
    private CursoJpaRepository jpaRepository;

    @InjectMocks
    private CursoPersistenceAdapter adapter;

    private CursoEntity entity() {
        return CursoEntity.builder().id(1L).nombre("1A").anioEscolar(2026).profesorJefeUuid(null).build();
    }

    @Test
    @DisplayName("guardar persiste y retorna dominio")
    void guardar_persisteYRetorna() {
        Curso c = new Curso(null, "1A", 2026, null);
        when(jpaRepository.save(any(CursoEntity.class))).thenReturn(entity());

        Curso result = adapter.guardar(c);

        assertThat(result.getNombre()).isEqualTo("1A");
    }

    @Test
    @DisplayName("buscarPorId encuentra curso")
    void buscarPorId_existe_retornaCurso() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity()));

        Optional<Curso> result = adapter.buscarPorId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("1A");
    }

    @Test
    @DisplayName("buscarPorId no encuentra retorna empty")
    void buscarPorId_noExiste_retornaEmpty() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(adapter.buscarPorId(99L)).isEmpty();
    }

    @Test
    @DisplayName("listarTodos retorna lista")
    void listarTodos_retornaLista() {
        when(jpaRepository.findAll()).thenReturn(List.of(entity()));

        assertThat(adapter.listarTodos()).hasSize(1);
    }

    @Test
    @DisplayName("existePorId retorna true si existe")
    void existePorId_existe_retornaTrue() {
        when(jpaRepository.existsById(1L)).thenReturn(true);

        assertThat(adapter.existePorId(1L)).isTrue();
    }

    @Test
    @DisplayName("existePorId retorna false si no existe")
    void existePorId_noExiste_retornaFalse() {
        when(jpaRepository.existsById(99L)).thenReturn(false);

        assertThat(adapter.existePorId(99L)).isFalse();
    }

    @Test
    @DisplayName("toDomain con profesorJefeUuid")
    void toDomain_conProfesorJefe_mapeaCorrecto() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        CursoEntity e = CursoEntity.builder().id(2L).nombre("2B").anioEscolar(2026).profesorJefeUuid(uuid).build();
        when(jpaRepository.findById(2L)).thenReturn(Optional.of(e));

        Optional<Curso> result = adapter.buscarPorId(2L);

        assertThat(result).isPresent();
        assertThat(result.get().getProfesorJefeUuid()).isEqualTo(uuid);
    }
}
