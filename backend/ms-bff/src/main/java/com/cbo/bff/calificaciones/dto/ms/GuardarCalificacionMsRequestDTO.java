package com.cbo.bff.calificaciones.dto.ms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuardarCalificacionMsRequestDTO {
    private String usuarioUuid;
    private Long asignaturaId;
    private Double nota1;
    private Double nota2;
    private Double nota3;
}
