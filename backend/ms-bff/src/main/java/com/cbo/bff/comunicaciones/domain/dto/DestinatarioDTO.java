package com.cbo.bff.comunicaciones.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DestinatarioDTO {
    private String id;
    private String nombre;
}
