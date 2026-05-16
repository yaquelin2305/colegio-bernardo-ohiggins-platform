package com.cbo.bff.gestionacademica.domain.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoletinDto {
    private UUID estudianteUuid;
    private String nombreCompleto;
    private List<CalificacionResumenDto> calificaciones;
    private Double promedioGeneral;
    private Double porcentajeAsistencia;
}
