package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AsignaturaResponse {
    private Long id;
    private String nombre;
    private Integer horasSemanales;
}
