package com.cbo.bff.asistencia.infrastructure.output.feign.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AsistenciaMsResponseDTO {
    private Long id;
    private String estudianteId;
    private String cursoId;
    private LocalDate fecha;
    private String estado;
    private String observacion;
}
