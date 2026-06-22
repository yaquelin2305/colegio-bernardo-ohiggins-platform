package cl.duoc.colegio.academico.infrastructure.factory;

import cl.duoc.colegio.academico.domain.model.AcademicReport;
import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.domain.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AcademicReportFactory — Pruebas Unitarias")
class AcademicReportFactoryTest {

    private static final UUID UUID_TEST = UUID.randomUUID();

    private Student student() {
        return new Student(1L, "12345678-9", "Juan", "Pérez", 3);
    }

    private Grade grade(double nota) {
        return new Grade(null, UUID_TEST, 5L, nota, "PRUEBA", null);
    }

    @Test
    void crear_sinAlertas_rendimientoBuenoYAsistenciaCompleta() {
        List<Grade> grades = List.of(grade(6.0), grade(5.5));
        Student s = student();

        AcademicReport report = AcademicReportFactory.crear(s, grades, 100.0);

        assertThat(report.getAlerta()).isEqualTo(AcademicReport.TipoAlerta.SIN_ALERTA);
        assertThat(report.getPromedio()).isEqualTo(5.75);
        assertThat(report.getMensajeAlerta()).contains("buen rendimiento");
    }

    @Test
    void crear_alertaRendimiento_promedioBajo4() {
        List<Grade> grades = List.of(grade(3.0), grade(3.5));
        Student s = student();

        AcademicReport report = AcademicReportFactory.crear(s, grades, 100.0);

        assertThat(report.getAlerta()).isEqualTo(AcademicReport.TipoAlerta.ALERTA_RENDIMIENTO);
        assertThat(report.getMensajeAlerta()).contains("bajo el mínimo");
    }

    @Test
    void crear_alertaAsistencia_asistenciaBaja85() {
        List<Grade> grades = List.of(grade(6.0));
        Student s = student();

        AcademicReport report = AcademicReportFactory.crear(s, grades, 80.0);

        assertThat(report.getAlerta()).isEqualTo(AcademicReport.TipoAlerta.ALERTA_ASISTENCIA);
        assertThat(report.getMensajeAlerta()).contains("asistencia");
    }

    @Test
    void crear_alertaCritica_rendimientoYAsistenciaBajas() {
        List<Grade> grades = List.of(grade(3.0));
        Student s = student();

        AcademicReport report = AcademicReportFactory.crear(s, grades, 60.0);

        assertThat(report.getAlerta()).isEqualTo(AcademicReport.TipoAlerta.ALERTA_CRITICA);
        assertThat(report.getMensajeAlerta()).contains("CRÍTICA");
    }

    @Test
    void crear_sinNotas_promedioCeroSinAlertaRendimiento() {
        List<Grade> grades = Collections.emptyList();
        Student s = student();

        AcademicReport report = AcademicReportFactory.crear(s, grades, 100.0);

        assertThat(report.getPromedio()).isEqualTo(0.0);
        assertThat(report.getAlerta()).isEqualTo(AcademicReport.TipoAlerta.SIN_ALERTA);
    }

    @Test
    void crear_asignaturasReprobadas_enLista() {
        List<Grade> grades = List.of(grade(3.0), grade(6.0), grade(3.5));
        Student s = student();

        AcademicReport report = AcademicReportFactory.crear(s, grades, 100.0);

        assertThat(report.getAsignaturasReprobadas()).hasSize(1);
        assertThat(report.getAsignaturasReprobadas()).contains(5L);
    }
}
