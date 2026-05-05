package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entidad JPA — Matrícula en base de datos.
 * Tabla M:N entre estudiante (UUID lógico) y curso.
 */
@Entity
@Table(
    name = "matriculas",
    schema = "academico_schema",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_uuid", "curso_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MatriculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_uuid", nullable = false, columnDefinition = "uuid")
    private UUID usuarioUuid;

    @Column(name = "curso_id", nullable = false)
    private Long cursoId;
}
