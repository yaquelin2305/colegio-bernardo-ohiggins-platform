package cl.duoc.colegio.bff.dto;

import lombok.*;

/** Resumen de calificaciones por asignatura para el boletín */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalificacionResumenDto {
    private Long asignaturaId;
    private String asignaturaNombre;
    private Double nota1;
    private Double nota2;
    private Double nota3;
    private Double promedio;
}
