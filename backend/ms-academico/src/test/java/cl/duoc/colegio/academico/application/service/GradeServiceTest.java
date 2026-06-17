package cl.duoc.colegio.academico.application.service;

import cl.duoc.colegio.academico.application.port.out.GradeRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.StudentRepositoryPort;
import cl.duoc.colegio.academico.domain.exception.GradeNotFoundException;
import cl.duoc.colegio.academico.domain.exception.StudentNotFoundException;
import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.domain.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * Tests unitarios para GradeService.
 * Mockea los puertos de salida — prueba SOLO la lógica de aplicación.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GradeService — Casos de uso de notas")
class GradeServiceTest {

    private static final UUID TEST_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID UUID_INEXISTENTE = UUID.fromString("99999999-9999-9999-9999-999999999999");
    private static final Long ASIGNATURA_ID = 100L;
    private static final Long ASIGNATURA_HISTORIA_ID = 200L;
    private static final Long ASIGNATURA_ARTE_ID = 300L;

    @Mock
    private GradeRepositoryPort gradeRepository;

    @Mock
    private StudentRepositoryPort studentRepository;

    @InjectMocks
    private GradeService gradeService;

    private Student studentMock;
    private Grade gradeMock;

    @BeforeEach
    void setUp() {
        studentMock = new Student(1L, "12345678-9", "Juan", "Pérez", 8);
        gradeMock = new Grade(1L, TEST_UUID, ASIGNATURA_ID, 5.0, "PRUEBA", "Test");
    }

    @Nested
    @DisplayName("calcularPromedioEstudiante()")
    class CalcularPromedio {

        @Test
        @DisplayName("Retorna 0.0 cuando el estudiante no tiene notas")
        void sinNotas_retornaCero() {
            given(studentRepository.buscarPorUsuarioUuid(TEST_UUID)).willReturn(Optional.of(studentMock));
            given(gradeRepository.buscarPorUsuarioUuid(TEST_UUID)).willReturn(List.of());

            double promedio = gradeService.calcularPromedioEstudiante(TEST_UUID);

            assertThat(promedio).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Calcula promedio correcto de múltiples notas")
        void variasNotas_promedioExacto() {
            Grade g1 = new Grade(1L, TEST_UUID, ASIGNATURA_ID, 4.0, "PRUEBA", null);
            Grade g2 = new Grade(2L, TEST_UUID, ASIGNATURA_ID, 6.0, "PRUEBA", null);
            Grade g3 = new Grade(3L, TEST_UUID, ASIGNATURA_ID, 5.0, "PRUEBA", null);

            given(studentRepository.buscarPorUsuarioUuid(TEST_UUID)).willReturn(Optional.of(studentMock));
            given(gradeRepository.buscarPorUsuarioUuid(TEST_UUID)).willReturn(List.of(g1, g2, g3));

            double promedio = gradeService.calcularPromedioEstudiante(TEST_UUID);

            assertThat(promedio).isEqualTo(5.0);
        }

        @Test
        @DisplayName("Lanza StudentNotFoundException si el estudiante no existe")
        void estudianteInexistente_lanzaExcepcion() {
            given(studentRepository.buscarPorUsuarioUuid(UUID_INEXISTENTE)).willReturn(Optional.empty());

            assertThatExceptionOfType(StudentNotFoundException.class)
                .isThrownBy(() -> gradeService.calcularPromedioEstudiante(UUID_INEXISTENTE));
        }
    }

    @Nested
    @DisplayName("registrarNota()")
    class RegistrarNota {

        @Test
        @DisplayName("Registra nota correctamente cuando el estudiante existe")
        void estudianteExiste_registraNota() {
            given(studentRepository.buscarPorUsuarioUuid(TEST_UUID)).willReturn(Optional.of(studentMock));
            given(gradeRepository.guardar(any(Grade.class))).willReturn(gradeMock);

            Grade result = gradeService.registrarNota(gradeMock);

            assertThat(result).isNotNull();
            assertThat(result.getNota()).isEqualTo(5.0);
            then(gradeRepository).should(times(1)).guardar(any(Grade.class));
        }

        @Test
        @DisplayName("Lanza StudentNotFoundException si el estudiante no existe")
        void estudianteInexistente_lanzaExcepcion() {
            Grade gradeConStudentInexistente = new Grade(null, UUID_INEXISTENTE, ASIGNATURA_ID,
                    5.0, "PRUEBA", null);
            given(studentRepository.buscarPorUsuarioUuid(UUID_INEXISTENTE)).willReturn(Optional.empty());

            assertThatExceptionOfType(StudentNotFoundException.class)
                .isThrownBy(() -> gradeService.registrarNota(gradeConStudentInexistente));

            then(gradeRepository).should(never()).guardar(any());
        }
    }

    @Nested
    @DisplayName("eliminarNota()")
    class EliminarNota {

        @Test
        @DisplayName("Elimina nota correctamente cuando existe")
        void notaExiste_eliminaCorrectamente() {
            given(gradeRepository.buscarPorId(1L)).willReturn(Optional.of(gradeMock));

            gradeService.eliminarNota(1L);

            then(gradeRepository).should(times(1)).eliminar(1L);
        }

        @Test
        @DisplayName("Lanza GradeNotFoundException si la nota no existe")
        void notaInexistente_lanzaExcepcion() {
            given(gradeRepository.buscarPorId(99L)).willReturn(Optional.empty());

            assertThatExceptionOfType(GradeNotFoundException.class)
                .isThrownBy(() -> gradeService.eliminarNota(99L));
        }
    }

    @Nested
    @DisplayName("calcularPromedioEstudiantePorAsignatura()")
    class CalcularPromedioPorAsignatura {

        @Test
        @DisplayName("Retorna promedio correcto para una asignatura específica")
        void asignaturaConNotas_promedioExacto() {
            Grade g1 = new Grade(1L, TEST_UUID, ASIGNATURA_HISTORIA_ID, 5.0, "PRUEBA", null);
            Grade g2 = new Grade(2L, TEST_UUID, ASIGNATURA_HISTORIA_ID, 7.0, "TAREA", null);

            given(gradeRepository.buscarPorUsuarioUuidYAsignaturaId(TEST_UUID, ASIGNATURA_HISTORIA_ID))
                .willReturn(List.of(g1, g2));

            double promedio = gradeService.calcularPromedioEstudiantePorAsignatura(TEST_UUID, ASIGNATURA_HISTORIA_ID);

            assertThat(promedio).isEqualTo(6.0);
        }

        @Test
        @DisplayName("Retorna 0.0 si no hay notas en esa asignatura")
        void sinNotasEnAsignatura_retornaCero() {
            given(gradeRepository.buscarPorUsuarioUuidYAsignaturaId(TEST_UUID, ASIGNATURA_ARTE_ID))
                .willReturn(List.of());

            double promedio = gradeService.calcularPromedioEstudiantePorAsignatura(TEST_UUID, ASIGNATURA_ARTE_ID);

            assertThat(promedio).isEqualTo(0.0);
        }
    }
}
