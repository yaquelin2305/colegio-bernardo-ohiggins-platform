package com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto;

import lombok.Data;

@Data
public class AnotacionRequestDTO {
    private String estudianteId;
    private String tipo;
    private String descripcion;
}
