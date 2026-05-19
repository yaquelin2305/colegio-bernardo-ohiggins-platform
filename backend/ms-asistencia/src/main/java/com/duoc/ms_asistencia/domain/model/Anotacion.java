package com.duoc.ms_asistencia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Anotacion {
    private Long id;
    private String estudianteId;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
}
