package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entidad JPA — Asignación Docente en base de datos.
 * Un docente queda asignado a un curso para dictar una asignatura.
 */
@Entity
@Table(
    name = "asignacion_docente",
    schema = "academico_schema",
    uniqueConstraints = @UniqueConstraint(columnNames = {"docente_uuid", "curso_id", "asignatura_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AsignacionDocenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "docente_uuid", nullable = false, columnDefinition = "uuid")
    private UUID docenteUuid;

    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    @Column(name = "asignatura_id", nullable = false)
    private Long asignaturaId;
}
