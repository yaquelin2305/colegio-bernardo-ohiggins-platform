package com.duoc.ms_comunicaciones.infrastructure.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComunicacionRequestDTO {
    private String destinatario; 
    private String asunto;
    private String mensaje;
    private String canal;    
}