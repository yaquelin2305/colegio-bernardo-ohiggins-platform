package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CursoResponse {
    private Long id;
    private String nombre;
    private Integer anioEscolar;
    private UUID profesorJefeUuid;
}
