package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

/**
 * DTO de request para PUT /calificaciones/guardar.
 * Recibe las 3 notas y delega el cálculo del promedio al servicio.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CalificacionesRequest {

    @NotNull(message = "El UUID del estudiante es obligatorio")
    private UUID usuarioUuid;

    @NotNull(message = "El ID de la asignatura es obligatorio")
    private Long asignaturaId;

    @NotNull(message = "nota1 es obligatoria")
    @DecimalMin("1.0") @DecimalMax("7.0")
    private Double nota1;

    /** Opcionales — pueden guardarse en etapas */
    @DecimalMin("1.0") @DecimalMax("7.0")
    private Double nota2;

    @DecimalMin("1.0") @DecimalMax("7.0")
    private Double nota3;
}
