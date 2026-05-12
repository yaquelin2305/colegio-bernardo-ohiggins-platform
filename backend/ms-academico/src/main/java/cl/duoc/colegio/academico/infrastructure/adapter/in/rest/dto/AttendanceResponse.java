package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttendanceResponse {
    private Long id;
    private Long studentId;
    private String asignatura;
    private LocalDate fecha;
    private Boolean presente;
    private String justificacion;
}
