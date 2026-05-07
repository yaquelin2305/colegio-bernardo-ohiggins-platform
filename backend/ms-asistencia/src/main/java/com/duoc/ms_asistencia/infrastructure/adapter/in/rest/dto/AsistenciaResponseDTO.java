package com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AsistenciaResponseDTO {
    private Long id;
    private String estudianteId;
    private String cursoId;
    private LocalDate fecha;
    private String estado;
    private String observacion;
}