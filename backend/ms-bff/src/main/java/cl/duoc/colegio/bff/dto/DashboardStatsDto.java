package cl.duoc.colegio.bff.dto;

import lombok.*;

/** DTO estadísticas del dashboard */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStatsDto {
    private Long totalEstudiantes;
    private Long totalDocentes;
    private Long totalCursos;
    private Long totalAsignaturas;
    private Double promedioGeneralInstitucion;
}
