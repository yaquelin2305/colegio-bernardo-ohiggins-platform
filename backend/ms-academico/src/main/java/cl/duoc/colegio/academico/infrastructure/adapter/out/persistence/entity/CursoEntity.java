package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entidad JPA — Curso en base de datos.
 */
@Entity
@Table(name = "cursos", schema = "academico_schema")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "anio_escolar", nullable = false)
    private Integer anioEscolar;

    /**
     * FK lógica al UUID del docente/profesor jefe en MS-Usuario.
     */
    @Column(name = "profesor_jefe_uuid", columnDefinition = "uuid")
    private UUID profesorJefeUuid;
}
