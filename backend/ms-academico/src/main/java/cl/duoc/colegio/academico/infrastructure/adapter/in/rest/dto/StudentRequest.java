package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO de entrada para crear/actualizar estudiantes.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StudentRequest {

    @NotBlank(message = "El RUT es obligatorio")
    @Size(min = 8, max = 12, message = "RUT inválido")
    private String rut;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String apellido;

    @NotNull(message = "El curso es obligatorio")
    @Min(value = 1, message = "Curso mínimo: 1")
    @Max(value = 12, message = "Curso máximo: 12")
    private Integer curso;
}
