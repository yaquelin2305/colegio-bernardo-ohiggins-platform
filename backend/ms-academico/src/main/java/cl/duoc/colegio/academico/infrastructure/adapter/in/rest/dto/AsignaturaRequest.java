package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AsignaturaRequest {

    @NotBlank(message = "El nombre de la asignatura es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotNull(message = "Las horas semanales son obligatorias")
    @Min(value = 1, message = "Mínimo 1 hora semanal")
    @Max(value = 45, message = "Máximo 45 horas semanales")
    private Integer horasSemanales;
}
