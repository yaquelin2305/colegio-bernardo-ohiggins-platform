package com.cbo.bff.calificaciones.dto.ms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalificacionMsResponseDTO {
    private String usuarioUuid;
    private Double nota1;
    private Double nota2;
    private Double nota3;
    private Double promedio;
}
