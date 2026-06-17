package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AcademicReportResponse {
    private Long studentId;
    private String nombreEstudiante;
    private String curso;
    private Double promedio;
    private Double porcentajeAsistencia;
    private String alerta;
    private String mensajeAlerta;
    private LocalDate fechaGeneracion;
    private List<Long> asignaturasReprobadas;
}
