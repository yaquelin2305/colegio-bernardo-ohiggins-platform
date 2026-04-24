package cl.duoc.colegio.academico.infrastructure.factory;

import cl.duoc.colegio.academico.domain.model.AcademicReport;
import cl.duoc.colegio.academico.domain.model.AcademicReport.TipoAlerta;
import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.domain.model.Student;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PATRÓN: Factory Method
 *
 * Centraliza la creación de AcademicReport.
 * La lógica de qué tipo de alerta generar vive AQUÍ — no dispersa en servicios.
 *
 * Extensión futura: agregar nuevos tipos de reporte sin modificar servicios existentes
 * (Open/Closed Principle — SOLID).
 */
public class AcademicReportFactory {

    private AcademicReportFactory() {
        // Utility class — no instanciar
    }

    /**
     * Factory Method principal.
     * Crea el reporte evaluando las condiciones del estudiante.
     */
    public static AcademicReport crear(Student student, List<Grade> grades,
                                        double porcentajeAsistencia) {
        double promedio = calcularPromedio(grades);
        boolean bajoRendimiento = promedio < 4.0 && !grades.isEmpty();
        boolean bajaAsistencia = porcentajeAsistencia < 85.0;

        TipoAlerta alerta = determinarAlerta(bajoRendimiento, bajaAsistencia);
        String mensaje = generarMensaje(alerta, promedio, porcentajeAsistencia);

        List<String> asignaturasReprobadas = grades.stream()
                .filter(g -> !g.esAprobatoria())
                .map(Grade::getAsignatura)
                .distinct()
                .collect(Collectors.toList());

        return new AcademicReport(
                student.getId(),
                student.getNombreCompleto(),
                String.valueOf(student.getCurso()),
                promedio,
                porcentajeAsistencia,
                alerta,
                mensaje,
                asignaturasReprobadas
        );
    }

    private static double calcularPromedio(List<Grade> grades) {
        if (grades.isEmpty()) return 0.0;
        return grades.stream()
                .mapToDouble(Grade::getNota)
                .average()
                .orElse(0.0);
    }

    private static TipoAlerta determinarAlerta(boolean bajoRendimiento, boolean bajaAsistencia) {
        if (bajoRendimiento && bajaAsistencia) return TipoAlerta.ALERTA_CRITICA;
        if (bajoRendimiento) return TipoAlerta.ALERTA_RENDIMIENTO;
        if (bajaAsistencia) return TipoAlerta.ALERTA_ASISTENCIA;
        return TipoAlerta.SIN_ALERTA;
    }

    private static String generarMensaje(TipoAlerta alerta, double promedio,
                                          double porcentajeAsistencia) {
        return switch (alerta) {
            case ALERTA_CRITICA -> String.format(
                "⚠️ ALERTA CRÍTICA: Promedio %.1f (bajo 4.0) y asistencia %.1f%% (bajo 85%%). " +
                "Riesgo alto de repitencia.", promedio, porcentajeAsistencia);
            case ALERTA_RENDIMIENTO -> String.format(
                "⚠️ ALERTA RENDIMIENTO: Promedio %.1f está bajo el mínimo aprobatorio de 4.0.",
                promedio);
            case ALERTA_ASISTENCIA -> String.format(
                "⚠️ ALERTA ASISTENCIA: %.1f%% de asistencia. Mínimo requerido: 85%%.",
                porcentajeAsistencia);
            case SIN_ALERTA -> "✅ Estudiante con buen rendimiento académico.";
        };
    }
}
