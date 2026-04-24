package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO de entrada para registrar/actualizar notas.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GradeRequest {

    @NotNull(message = "studentId es obligatorio")
    private Long studentId;

    @NotBlank(message = "La asignatura es obligatoria")
    @Size(max = 100)
    private String asignatura;

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "1.0", message = "Nota mínima: 1.0")
    @DecimalMax(value = "7.0", message = "Nota máxima: 7.0")
    private Double nota;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo; // PRUEBA, TAREA, EXAMEN, TRABAJO

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @Size(max = 255)
    private String descripcion;
}
