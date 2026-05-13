package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PromedioAsignaturaResponse {
    private UUID usuarioUuid;
    private Long asignaturaId;
    private Double promedio;
}
