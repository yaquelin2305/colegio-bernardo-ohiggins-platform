package com.cbo.bff.asistencia.dto.ms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaMsRequestDTO {
    private String estudianteId;
    private String cursoId;
    private String estado;
    private String observacion;
    private LocalDate fecha;
}
