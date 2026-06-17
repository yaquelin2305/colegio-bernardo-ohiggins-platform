package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

/**
 * DTO de entrada para registrar/actualizar notas.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GradeRequest {

    @NotNull(message = "usuarioUuid es obligatorio")
    private UUID usuarioUuid;

    @NotNull(message = "asignaturaId es obligatorio")
    private Long asignaturaId;

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "1.0", message = "Nota mínima: 1.0")
    @DecimalMax(value = "7.0", message = "Nota máxima: 7.0")
    private Double nota;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo; // PRUEBA, TAREA, EXAMEN, TRABAJO

    @Size(max = 255)
    private String descripcion;
}
