package com.cbo.bff.asistencia.dto.ms;

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
