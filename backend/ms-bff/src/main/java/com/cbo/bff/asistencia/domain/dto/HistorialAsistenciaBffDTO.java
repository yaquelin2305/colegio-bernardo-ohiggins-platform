package com.cbo.bff.asistencia.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistorialAsistenciaBffDTO {
    private Long id;
    private LocalDate fecha;
    private String estado;
    private String anotacion;
}
