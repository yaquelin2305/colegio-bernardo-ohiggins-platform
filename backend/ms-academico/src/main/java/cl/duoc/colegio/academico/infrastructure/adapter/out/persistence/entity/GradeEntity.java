package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidad JPA — Nota académica en base de datos.
 */
@Entity
@Table(name = "notas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(nullable = false, length = 100)
    private String asignatura;

    @Column(nullable = false)
    private Double nota;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 255)
    private String descripcion;
}
