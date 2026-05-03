package cl.duoc.colegio.bff.dto;

import lombok.*;
import java.util.UUID;

/** DTO recibido desde MS-Académico para calificaciones */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CalificacionesAcademicoDto {
    private UUID usuarioUuid;
    private Long asignaturaId;
    private Double nota1;
    private Double nota2;
    private Double nota3;
    private Double promedio;
}
