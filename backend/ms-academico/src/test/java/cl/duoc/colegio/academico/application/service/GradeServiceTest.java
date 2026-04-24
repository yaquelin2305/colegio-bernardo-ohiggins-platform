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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        gradeMock = new Grade(1L, 1L, "Matemáticas", 5.0, "PRUEBA", LocalDate.now(), "Test");
    }

    @Nested
    @DisplayName("calcularPromedioEstudiante()")
    class CalcularPromedio {

        @Test
        @DisplayName("Retorna 0.0 cuando el estudiante no tiene notas")
        void sinNotas_retornaCero() {
            given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(studentMock));
            given(gradeRepository.buscarPorStudentId(1L)).willReturn(List.of());

            double promedio = gradeService.calcularPromedioEstudiante(1L);

            assertThat(promedio).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Calcula promedio correcto de múltiples notas")
        void variasNotas_promedioExacto() {
            Grade g1 = new Grade(1L, 1L, "Matemáticas", 4.0, "PRUEBA", LocalDate.now(), null);
            Grade g2 = new Grade(2L, 1L, "Matemáticas", 6.0, "PRUEBA", LocalDate.now(), null);
            Grade g3 = new Grade(3L, 1L, "Matemáticas", 5.0, "PRUEBA", LocalDate.now(), null);

            given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(studentMock));
            given(gradeRepository.buscarPorStudentId(1L)).willReturn(List.of(g1, g2, g3));

            double promedio = gradeService.calcularPromedioEstudiante(1L);

            assertThat(promedio).isEqualTo(5.0);
        }

        @Test
        @DisplayName("Lanza StudentNotFoundException si el estudiante no existe")
        void estudianteInexistente_lanzaExcepcion() {
            given(studentRepository.buscarPorId(99L)).willReturn(Optional.empty());

            assertThatExceptionOfType(StudentNotFoundException.class)
                .isThrownBy(() -> gradeService.calcularPromedioEstudiante(99L));
        }
    }

    @Nested
    @DisplayName("registrarNota()")
    class RegistrarNota {

        @Test
        @DisplayName("Registra nota correctamente cuando el estudiante existe")
        void estudianteExiste_registraNota() {
            given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(studentMock));
            given(gradeRepository.guardar(any(Grade.class))).willReturn(gradeMock);

            Grade result = gradeService.registrarNota(gradeMock);

            assertThat(result).isNotNull();
            assertThat(result.getNota()).isEqualTo(5.0);
            then(gradeRepository).should(times(1)).guardar(any(Grade.class));
        }

        @Test
        @DisplayName("Lanza StudentNotFoundException si el estudiante no existe")
        void estudianteInexistente_lanzaExcepcion() {
            Grade gradeConStudentInexistente = new Grade(null, 99L, "Matemáticas",
                    5.0, "PRUEBA", LocalDate.now(), null);
            given(studentRepository.buscarPorId(99L)).willReturn(Optional.empty());

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
            Grade g1 = new Grade(1L, 1L, "Historia", 5.0, "PRUEBA", LocalDate.now(), null);
            Grade g2 = new Grade(2L, 1L, "Historia", 7.0, "TAREA", LocalDate.now(), null);

            given(gradeRepository.buscarPorStudentIdYAsignatura(1L, "Historia"))
                .willReturn(List.of(g1, g2));

            double promedio = gradeService.calcularPromedioEstudiantePorAsignatura(1L, "Historia");

            assertThat(promedio).isEqualTo(6.0);
        }

        @Test
        @DisplayName("Retorna 0.0 si no hay notas en esa asignatura")
        void sinNotasEnAsignatura_retornaCero() {
            given(gradeRepository.buscarPorStudentIdYAsignatura(1L, "Arte"))
                .willReturn(List.of());

            double promedio = gradeService.calcularPromedioEstudiantePorAsignatura(1L, "Arte");

            assertThat(promedio).isEqualTo(0.0);
        }
    }
}
