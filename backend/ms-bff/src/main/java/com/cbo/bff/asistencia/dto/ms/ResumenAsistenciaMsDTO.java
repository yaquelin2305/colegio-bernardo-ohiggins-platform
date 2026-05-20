package com.cbo.bff.asistencia.dto.ms;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResumenAsistenciaMsDTO {
    private String cursoId;
    private LocalDate fecha;
    private int totalPresentes;
    private int totalAusentes;
    private int totalJustificados;
    private int total;
    private double porcentajeAsistencia;
}
