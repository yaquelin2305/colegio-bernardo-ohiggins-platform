package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CursoRequest {

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 50)
    private String nombre;

    @NotNull(message = "El año escolar es obligatorio")
    @Min(value = 2000, message = "Año escolar inválido")
    private Integer anioEscolar;

    /** Opcional — puede no tener profesor jefe asignado aún */
    private UUID profesorJefeUuid;
}
