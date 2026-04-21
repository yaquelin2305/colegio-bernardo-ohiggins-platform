package com.colegio.notificacion.infrastructure.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionDTO {
    private String emisor;
    private String destinatario;
    private String asunto;
    private String mensaje;
    private LocalDateTime fechaEnvio;
}