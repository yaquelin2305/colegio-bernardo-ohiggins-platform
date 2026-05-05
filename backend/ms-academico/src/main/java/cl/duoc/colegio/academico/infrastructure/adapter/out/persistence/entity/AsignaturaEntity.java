package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA — Asignatura en base de datos.
 */
@Entity
@Table(name = "asignaturas", schema = "academico_schema")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AsignaturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "horas_semanales", nullable = false)
    private Integer horasSemanales;
}
