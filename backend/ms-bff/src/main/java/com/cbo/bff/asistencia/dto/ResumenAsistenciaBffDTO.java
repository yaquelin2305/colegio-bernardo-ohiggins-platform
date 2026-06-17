package com.cbo.bff.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumenAsistenciaBffDTO {
    private int total;
    private int presentes;
    private int ausentes;
    private int totalJustificados;
    private double porcentaje;
    private String nombreCurso;
}
