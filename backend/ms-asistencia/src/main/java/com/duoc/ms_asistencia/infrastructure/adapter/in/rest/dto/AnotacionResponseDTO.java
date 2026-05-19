package com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AnotacionResponseDTO {
    private Long id;
    private String estudianteId;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
}
