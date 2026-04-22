package com.colegio.notificacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {
    private Long id;
    private String emisor;
    private String destinatario;
    private String asunto;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private boolean leido;
}