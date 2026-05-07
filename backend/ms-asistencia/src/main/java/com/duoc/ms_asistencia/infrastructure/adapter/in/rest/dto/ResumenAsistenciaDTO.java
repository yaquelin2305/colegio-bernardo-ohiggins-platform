package com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ResumenAsistenciaDTO {
    private String cursoId;
    private LocalDate fecha;
    private int totalPresentes;
    private int totalAusentes;
    private int totalJustificados;
    private int total;
    private double porcentajeAsistencia;
}
