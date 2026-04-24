package cl.duoc.colegio.academico.application.service;

import cl.duoc.colegio.academico.application.port.out.AttendanceRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.GradeRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.StudentRepositoryPort;
import cl.duoc.colegio.academico.domain.exception.StudentNotFoundException;
import cl.duoc.colegio.academico.domain.model.AcademicReport;
import cl.duoc.colegio.academico.domain.model.Attendance;
import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.domain.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
 * Tests unitarios para ReportService + AcademicReportFactory.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService — Generación de reportes académicos")
class ReportServiceTest {

    @Mock private StudentRepositoryPort studentRepository;
    @Mock private GradeRepositoryPort gradeRepository;
    @Mock private AttendanceRepositoryPort attendanceRepository;

    @InjectMocks
    private ReportService reportService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "12345678-9", "María", "González", 7);
    }

    @Test
    @DisplayName("Genera reporte SIN_ALERTA con buen promedio y buena asistencia")
    void buenEstudiante_sinAlerta() {
        given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(student));
        given(gradeRepository.buscarPorStudentId(1L)).willReturn(List.of(
            grade(5.5), grade(6.0), grade(6.5)
        ));
        given(attendanceRepository.buscarPorStudentId(1L)).willReturn(List.of(
            asistencia(true), asistencia(true), asistencia(true),
            asistencia(true), asistencia(true)
        ));

        AcademicReport report = reportService.generarReporteEstudiante(1L);

        assertThat(report.getAlerta()).isEqualTo(AcademicReport.TipoAlerta.SIN_ALERTA);
        assertThat(report.tieneAlerta()).isFalse();
        assertThat(report.getPromedio()).isGreaterThanOrEqualTo(4.0);
    }

    @Test
    @DisplayName("Genera ALERTA_RENDIMIENTO con promedio bajo 4.0")
    void promedioReprobatorio_alertaRendimiento() {
        given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(student));
        given(gradeRepository.buscarPorStudentId(1L)).willReturn(List.of(
            grade(2.0), grade(3.0), grade(3.5)
        ));
        given(attendanceRepository.buscarPorStudentId(1L)).willReturn(List.of(
            asistencia(true), asistencia(true), asistencia(true),
            asistencia(true), asistencia(true)
        ));

        AcademicReport report = reportService.generarReporteEstudiante(1L);

        assertThat(report.getAlerta()).isEqualTo(AcademicReport.TipoAlerta.ALERTA_RENDIMIENTO);
        assertThat(report.tieneAlerta()).isTrue();
    }

    @Test
    @DisplayName("Genera ALERTA_CRITICA con bajo rendimiento Y baja asistencia")
    void reprobadoYAusenteFrequente_alertaCritica() {
        given(studentRepository.buscarPorId(1L)).willReturn(Optional.of(student));
        given(gradeRepository.buscarPorStudentId(1L)).willReturn(List.of(
            grade(2.0), grade(2.5)
        ));
        // 60% asistencia — bajo 85%
        given(attendanceRepository.buscarPorStudentId(1L)).willReturn(List.of(
            asistencia(true), asistencia(true), asistencia(true),
            asistencia(false), asistencia(false)
        ));

        AcademicReport report = reportService.generarReporteEstudiante(1L);

        assertThat(report.getAlerta()).isEqualTo(AcademicReport.TipoAlerta.ALERTA_CRITICA);
        assertThat(report.getMensajeAlerta()).contains("ALERTA CRÍTICA");
    }

    @Test
    @DisplayName("Lanza StudentNotFoundException si el estudiante no existe")
    void estudianteInexistente_lanzaExcepcion() {
        given(studentRepository.buscarPorId(99L)).willReturn(Optional.empty());

        assertThatExceptionOfType(StudentNotFoundException.class)
            .isThrownBy(() -> reportService.generarReporteEstudiante(99L));
    }

    private Grade grade(double nota) {
        return new Grade(null, 1L, "Matemáticas", nota, "PRUEBA", LocalDate.now(), null);
    }

    private Attendance asistencia(boolean presente) {
        return new Attendance(null, 1L, "Matemáticas", LocalDate.now(), presente, null);
    }
}
