package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad JPA — Registro de asistencia en base de datos.
 */
@Entity
@Table(name = "asistencias", schema = "academico_schema")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia lógica al UUID del usuario/estudiante en MS-Usuario.
     */
    @Column(name = "usuario_uuid", nullable = false, columnDefinition = "uuid")
    private UUID usuarioUuid;

    /**
     * FK lógica a academico_schema.asignaturas.id
     */
    @Column(name = "asignatura_id", nullable = false)
    private Long asignaturaId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Boolean presente;

    @Column(length = 500)
    private String justificacion;
}
