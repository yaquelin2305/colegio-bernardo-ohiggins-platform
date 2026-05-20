package com.cbo.bff.asistencia.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AsistenciaRequestBffDTO {
    private String estudianteId;
    private String cursoId;
    private String estado;
    private String observacion;
    private LocalDate fecha;
}
