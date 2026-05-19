package com.cbo.bff.asistencia.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteAsistenciaBffDTO {
    private Long id;
    private String estudianteId;
    private String nombre;
    private String curso;
    private String estado;
    private String hora;
}
