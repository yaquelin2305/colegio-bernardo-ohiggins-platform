package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AsistenciaPorcentajeResponse {
    private Long studentId;
    private Double porcentajeAsistencia;
    private Boolean enRiesgoRepitencia;
}
