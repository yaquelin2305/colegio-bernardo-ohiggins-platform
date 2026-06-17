package com.cbo.bff.calificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalificacionBffResponseDTO {
    private String id;
    private String nombre;
    private Double nota1;
    private Double nota2;
    private Double nota3;
    private Double promedio;
}
