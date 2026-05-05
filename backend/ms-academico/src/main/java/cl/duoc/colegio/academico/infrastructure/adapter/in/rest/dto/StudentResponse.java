package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import lombok.*;

/**
 * DTO de salida para respuestas de Estudiante.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StudentResponse {
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private Integer curso;
    private String nombreCompleto;
}
