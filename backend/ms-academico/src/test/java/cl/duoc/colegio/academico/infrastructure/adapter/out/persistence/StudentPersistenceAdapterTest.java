package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.domain.model.Student;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.StudentEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper.StudentPersistenceMapper;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.StudentJpaRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentPersistenceAdapter — Pruebas Unitarias")
class StudentPersistenceAdapterTest {

    @Mock
    private StudentJpaRepository jpaRepository;

    @Mock
    private StudentPersistenceMapper mapper;

    @InjectMocks
    private StudentPersistenceAdapter adapter;

    private StudentEntity entity = StudentEntity.builder().id(1L).rut("11111111-1").nombre("A").apellido("B").curso(3).build();
    private Student domain = new Student(1L, "11111111-1", "A", "B", 3);

    @Test
    void guardar_persisteYRetorna() {
        when(mapper.toEntity(any())).thenReturn(entity);
        when(jpaRepository.save(any())).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Student result = adapter.guardar(domain);

        assertThat(result.getRut()).isEqualTo("11111111-1");
    }

    @Test
    void buscarPorId_existe_retorna() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.buscarPorId(1L)).isPresent();
        assertThat(adapter.buscarPorId(1L).get().getRut()).isEqualTo("11111111-1");
    }

    @Test
    void buscarPorId_noExiste_retornaEmpty() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(adapter.buscarPorId(99L)).isEmpty();
    }

    @Test
    void buscarPorRut_existe_retorna() {
        when(jpaRepository.findByRut("11111111-1")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.buscarPorRut("11111111-1")).isPresent();
    }

    @Test
    void buscarPorRut_noExiste_retornaEmpty() {
        when(jpaRepository.findByRut("99999999-9")).thenReturn(Optional.empty());
        assertThat(adapter.buscarPorRut("99999999-9")).isEmpty();
    }

    @Test
    void buscarTodos_retornaLista() {
        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.buscarTodos()).hasSize(1);
    }

    @Test
    void buscarPorCurso_retornaLista() {
        when(jpaRepository.findByCurso(3)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.buscarPorCurso(3)).hasSize(1);
    }

    @Test
    void existePorRut_retornaTrue() {
        when(jpaRepository.existsByRut("11111111-1")).thenReturn(true);
        assertThat(adapter.existePorRut("11111111-1")).isTrue();
    }

    @Test
    void eliminar_llamaDeleteById() {
        adapter.eliminar(1L);
        verify(jpaRepository).deleteById(1L);
    }
}
