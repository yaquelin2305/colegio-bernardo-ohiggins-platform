package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.in.ReportUseCase;
import cl.duoc.colegio.academico.domain.model.AcademicReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST para Reportes Académicos.
 * Endpoint usado por el BFF para obtener el reporte consolidado de un estudiante.
 */
@RestController
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes", description = "Generación de reportes académicos y alertas")
public class ReportController {

    private final ReportUseCase reportUseCase;

    public ReportController(ReportUseCase reportUseCase) {
        this.reportUseCase = reportUseCase;
    }

    @GetMapping("/estudiante/{usuarioUuid}")
    @Operation(
        summary = "Generar reporte académico",
        description = "Genera un reporte completo con promedio, asistencia y alertas de repitencia para un estudiante"
    )
    public ResponseEntity<AcademicReport> generarReporte(@PathVariable UUID usuarioUuid) {
        return ResponseEntity.ok(reportUseCase.generarReporteEstudiante(usuarioUuid));
    }
}
