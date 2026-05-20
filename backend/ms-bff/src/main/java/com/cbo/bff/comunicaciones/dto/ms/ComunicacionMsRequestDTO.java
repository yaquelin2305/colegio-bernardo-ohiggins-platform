package com.cbo.bff.comunicaciones.dto.ms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComunicacionMsRequestDTO {
    private String destinatario;
    private String asunto;
    private String mensaje;
    private String canal;
    private String tipo;
}
