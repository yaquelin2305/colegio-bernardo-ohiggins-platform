package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO de entrada para registrar asistencia.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttendanceRequest {

    @NotNull(message = "studentId es obligatorio")
    private Long studentId;

    @NotBlank(message = "La asignatura es obligatoria")
    @Size(max = 100)
    private String asignatura;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El campo presente es obligatorio")
    private Boolean presente;

    @Size(max = 500)
    private String justificacion;
}
