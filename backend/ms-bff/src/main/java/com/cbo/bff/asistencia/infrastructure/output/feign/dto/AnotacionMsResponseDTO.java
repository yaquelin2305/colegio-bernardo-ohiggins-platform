package com.cbo.bff.asistencia.infrastructure.output.feign.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AnotacionMsResponseDTO {
    private Long id;
    private String estudianteId;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
}
