package com.cbo.bff.comunicaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MensajeBffDTO {
    private Long id;
    private String remitente;
    private String asunto;
    private String cuerpo;
    private String canal;
    private String tipo;
    private String fecha;
    private boolean leido;
}
