package com.cbo.bff.gestionacademica.dto.ms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioMsDTO {
    private String id;
    private String rut;
    private String email;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;
    private String pupiloUuid;
}
