package com.cbo.bff.calificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuardarCalificacionBffRequestDTO {
    private String usuarioUuid;
    private Long asignaturaId;
    private Double nota1;
    private Double nota2;
    private Double nota3;
}
