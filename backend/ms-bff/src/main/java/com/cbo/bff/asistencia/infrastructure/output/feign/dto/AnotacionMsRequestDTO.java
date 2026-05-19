package com.cbo.bff.asistencia.infrastructure.output.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnotacionMsRequestDTO {
    private String estudianteId;
    private String tipo;
    private String descripcion;
}
