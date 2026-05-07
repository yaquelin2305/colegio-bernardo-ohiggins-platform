package com.duoc.ms_asistencia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumenAsistencia {
    private String cursoId;
    private LocalDate fecha;
    private int totalPresentes;
    private int totalAusentes;
    private int totalJustificados;
    private int total;
    private double porcentajeAsistencia;
}
