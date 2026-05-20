package com.cbo.bff.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlumnoBffDTO {
    private String estudianteId;
    private String nombre;
    private String rut;
}
