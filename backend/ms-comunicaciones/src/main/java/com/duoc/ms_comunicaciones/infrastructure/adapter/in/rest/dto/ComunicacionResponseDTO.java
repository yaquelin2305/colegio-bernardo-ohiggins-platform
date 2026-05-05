package com.duoc.ms_comunicaciones.infrastructure.adapter.in.rest.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComunicacionResponseDTO {
    private Long mensajeId;
    private String usuarioId;
    private String destinatario;
    private String asunto;
    private String mensaje;
    private String canal;
    private LocalDateTime fechaEnvio;
    private boolean leido;
}