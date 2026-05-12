package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MatriculaResponse {
    private Long id;
    private UUID usuarioUuid;
    private Long cursoId;
}
