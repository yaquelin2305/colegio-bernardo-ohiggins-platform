package com.cbo.bff.asistencia.infrastructure.output.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JustificacionMsRequestDTO {
    private String motivo;
}
