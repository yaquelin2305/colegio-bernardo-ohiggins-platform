package com.cbo.bff.gestionacademica.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStatsDto {
    private Long totalEstudiantes;
    private Long totalDocentes;
    private Long totalCursos;
    private Long totalAsignaturas;
    private Double promedioGeneralInstitucion;
}
