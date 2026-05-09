package com.cbo.bff.comunicaciones.infrastructure.output.feign.dto;

import lombok.Data;

@Data
public class ComunicacionMsResponseDTO {
    private Long mensajeId;
    private String usuarioId;
    private String destinatario;
    private String asunto;
    private String mensaje;
    private String canal;
    private String tipo;
    private String fechaEnvio;
    private boolean leido;
}
