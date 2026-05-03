package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entidad JPA — Calificación académica en base de datos.
 *
 * Mapea a academico_schema.calificaciones siguiendo el ERD.
 * El modelo de dominio (Grade) mantiene una nota por registro y tipo flexible.
 * El GradeContractMapper transforma N notas → {nota_1, nota_2, nota_3, promedio}
 * para cumplir el contrato PUT /calificaciones/guardar.
 */
@Entity
@Table(name = "calificaciones", schema = "academico_schema")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia lógica al UUID del usuario/estudiante en MS-Usuario.
     */
    @Column(name = "usuario_uuid", nullable = false, columnDefinition = "uuid")
    private UUID usuarioUuid;

    /**
     * FK al id de academico_schema.asignaturas (validado por servicio, no constraint de BD).
     */
    @Column(name = "asignatura_id", nullable = false)
    private Long asignaturaId;

    @Column(name = "nota_1")
    private Double nota1;

    @Column(name = "nota_2")
    private Double nota2;

    @Column(name = "nota_3")
    private Double nota3;

    /**
     * Promedio calculado en la capa de servicio y persistido aquí.
     * Se recalcula cada vez que se guarda (PUT /calificaciones/guardar).
     */
    @Column(nullable = false)
    private Double promedio;
}
