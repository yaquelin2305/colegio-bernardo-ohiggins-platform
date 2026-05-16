package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.in.ReportUseCase;
import cl.duoc.colegio.academico.domain.model.AcademicReport;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.AcademicReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public ResponseEntity<AcademicReportResponse> generarReporte(@PathVariable UUID usuarioUuid) {
        return ResponseEntity.ok(toResponse(reportUseCase.generarReporteEstudiante(usuarioUuid)));
    }

    private AcademicReportResponse toResponse(AcademicReport r) {
        return AcademicReportResponse.builder()
                .studentId(r.getStudentId())
                .nombreEstudiante(r.getNombreEstudiante())
                .curso(r.getCurso())
                .promedio(r.getPromedio())
                .porcentajeAsistencia(r.getPorcentajeAsistencia())
                .alerta(r.getAlerta().name())
                .mensajeAlerta(r.getMensajeAlerta())
                .fechaGeneracion(r.getFechaGeneracion())
                .asignaturasReprobadas(r.getAsignaturasReprobadas())
                .build();
    }
}
