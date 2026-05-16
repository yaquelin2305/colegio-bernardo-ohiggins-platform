package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AsignacionDocenteResponse {
    private Long id;
    private UUID docenteUuid;
    private Long cursoId;
    private Long asignaturaId;
}
