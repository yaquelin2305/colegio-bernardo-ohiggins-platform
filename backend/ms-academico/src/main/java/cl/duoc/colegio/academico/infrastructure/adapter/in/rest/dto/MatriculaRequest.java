package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MatriculaRequest {

    @NotNull(message = "El UUID del estudiante (usuarioUuid) es obligatorio")
    private UUID usuarioUuid;

    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;
}
