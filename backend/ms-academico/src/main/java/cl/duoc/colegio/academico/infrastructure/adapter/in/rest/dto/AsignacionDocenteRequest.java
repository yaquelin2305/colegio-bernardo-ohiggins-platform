package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AsignacionDocenteRequest {

    @NotNull(message = "El UUID del docente es obligatorio")
    private UUID docenteUuid;

    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;

    @NotNull(message = "El ID de la asignatura es obligatorio")
    private Long asignaturaId;
}
