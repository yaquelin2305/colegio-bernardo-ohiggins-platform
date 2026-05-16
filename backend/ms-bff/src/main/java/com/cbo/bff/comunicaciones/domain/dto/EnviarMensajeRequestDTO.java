package com.cbo.bff.comunicaciones.domain.dto;

import lombok.Data;

@Data
public class EnviarMensajeRequestDTO {
    private String destinatario;
    private String asunto;
    private String mensaje;
    private String canal;
    private String tipo;
}
