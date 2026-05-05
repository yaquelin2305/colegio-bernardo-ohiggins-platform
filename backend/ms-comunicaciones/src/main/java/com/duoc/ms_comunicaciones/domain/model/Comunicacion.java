package com.duoc.ms_comunicaciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comunicacion {
    private Long id;
    private String usuarioId;  
    private String destinatario; 
    private String asunto;
    private String mensaje;
    private Canal canal;         
    private LocalDateTime fechaEnvio;
    private boolean leido;
}