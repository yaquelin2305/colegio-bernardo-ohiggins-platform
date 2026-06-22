package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.domain.model.Attendance;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AttendanceEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper.AttendancePersistenceMapper;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.AttendanceJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendancePersistenceAdapter — Pruebas Unitarias")
class AttendancePersistenceAdapterTest {

    @Mock
    private AttendanceJpaRepository jpaRepository;

    @Mock
    private AttendancePersistenceMapper mapper;

    @InjectMocks
    private AttendancePersistenceAdapter adapter;

    private AttendanceEntity entity = AttendanceEntity.builder()
            .id(1L).studentId(100L).asignatura("Matemáticas")
            .fecha(LocalDate.of(2026, 6, 1)).presente(true).justificacion(null)
            .build();
    private Attendance domain = new Attendance(1L, 100L, "Matemáticas", LocalDate.of(2026, 6, 1), true, null);

    @Test
    void guardar_persisteYRetorna() {
        when(mapper.toEntity(any())).thenReturn(entity);
        when(jpaRepository.save(any())).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Attendance result = adapter.guardar(domain);
        assertThat(result.isPresente()).isTrue();
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
    void buscarPorStudentId_retornaLista() {
        when(jpaRepository.findByStudentId(100L)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.buscarPorStudentId(100L)).hasSize(1);
    }

    @Test
    void buscarPorStudentIdYFecha_retornaLista() {
        LocalDate fecha = LocalDate.of(2026, 6, 1);
        when(jpaRepository.findByStudentIdAndFecha(100L, fecha)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        assertThat(adapter.buscarPorStudentIdYFecha(100L, fecha)).hasSize(1);
    }

    @Test
    void eliminar_llamaDeleteById() {
        adapter.eliminar(1L);
        verify(jpaRepository).deleteById(1L);
    }
}
