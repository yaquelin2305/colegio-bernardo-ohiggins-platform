package cl.duoc.colegio.academico.application.port.in;

import cl.duoc.colegio.academico.domain.model.AcademicReport;

/**
 * Puerto de entrada — Caso de uso: Generar Reporte Académico.
 * Orquesta notas y asistencia para producir un reporte con alertas.
 */
public interface ReportUseCase {

    /**
     * Genera un reporte académico completo para un estudiante.
     * Incluye promedio, asistencia y alertas de repitencia.
     */
    AcademicReport generarReporteEstudiante(Long studentId);
}
