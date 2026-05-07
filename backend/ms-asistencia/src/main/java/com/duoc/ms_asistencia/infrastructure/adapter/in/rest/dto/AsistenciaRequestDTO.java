package com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AsistenciaRequestDTO {
    private String estudianteId;
    private String cursoId;
    private String estado;
    private String observacion;
    private LocalDate fecha;
}