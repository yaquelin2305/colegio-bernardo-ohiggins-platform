package cl.duoc.colegio.academico.application.service;

import cl.duoc.colegio.academico.application.port.out.AttendanceRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.StudentRepositoryPort;
import cl.duoc.colegio.academico.domain.exception.StudentNotFoundException;
import cl.duoc.colegio.academico.domain.model.Attendance;
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
import static org.mockito.BDDMockito.*;

/**
 * Tests unitarios para AttendanceService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceService — Cálculo de asistencia y riesgo de repitencia")
class AttendanceServiceTest {

    @Mock
    private AttendanceRepositoryPort attendanceRepository;

    @Mock
    private StudentRepositoryPort studentRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Student studentMock;

    @BeforeEach
    void setUp() {
        studentMock = new Student(1L, "12345678-9", "Juan", "Pérez", 8);
    }

    @Nested
    @DisplayName("calcularPorcentajeAsistencia()")
    class PorcentajeAsistencia {

        @Test
        @DisplayName("Retorna 100.0 sin registros")
        void sinRegistros_cientoPorciento() {
            given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(studentMock));
            given(attendanceRepository.buscarPorStudentId(1L)).willReturn(List.of());

            assertThat(attendanceService.calcularPorcentajeAsistencia(1L)).isEqualTo(100.0);
        }

        @Test
        @DisplayName("75% con 3 presentes y 1 ausente")
        void tresPresentes_unAusente_75porciento() {
            given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(studentMock));
            given(attendanceRepository.buscarPorStudentId(1L)).willReturn(List.of(
                asistencia(true), asistencia(true), asistencia(true), asistencia(false)
            ));

            assertThat(attendanceService.calcularPorcentajeAsistencia(1L)).isEqualTo(75.0);
        }
    }

    @Nested
    @DisplayName("estaEnRiesgoRepitencia()")
    class RiesgoRepitencia {

        @Test
        @DisplayName("Retorna false cuando asistencia >= 85%")
        void altaAsistencia_sinRiesgo() {
            given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(studentMock));
            given(attendanceRepository.buscarPorStudentId(1L)).willReturn(List.of(
                asistencia(true), asistencia(true), asistencia(true),
                asistencia(true), asistencia(true)  // 100%
            ));

            assertThat(attendanceService.estaEnRiesgoRepitencia(1L)).isFalse();
        }

        @Test
        @DisplayName("Retorna true cuando asistencia < 85%")
        void bajaAsistencia_enRiesgo() {
            given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(studentMock));
            // 4 presentes de 5 = 80% < 85%
            given(attendanceRepository.buscarPorStudentId(1L)).willReturn(List.of(
                asistencia(true), asistencia(true),
                asistencia(true), asistencia(true), asistencia(false)
            ));

            assertThat(attendanceService.estaEnRiesgoRepitencia(1L)).isTrue();
        }

        @Test
        @DisplayName("Lanza excepción si estudiante no existe")
        void estudianteInexistente_lanzaExcepcion() {
            given(studentRepository.buscarPorId(99L)).willReturn(Optional.empty());

            assertThatExceptionOfType(StudentNotFoundException.class)
                .isThrownBy(() -> attendanceService.calcularPorcentajeAsistencia(99L));
        }
    }

    private Attendance asistencia(boolean presente) {
        return new Attendance(null, 1L, "Matemáticas", LocalDate.now(), presente, null);
    }
}
