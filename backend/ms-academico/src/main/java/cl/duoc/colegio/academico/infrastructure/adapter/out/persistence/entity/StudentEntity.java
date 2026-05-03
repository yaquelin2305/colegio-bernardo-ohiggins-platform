package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entidad JPA — Estudiante en base de datos.
 *
 * DISEÑO CROSS-SERVICE:
 * usuario_uuid = FK lógica al MS-Usuario (users_schema.usuarios.id).
 * No es una FK de BD real (microservicios no comparten BD),
 * pero es el puente de identidad entre ambos servicios.
 */
@Entity
@Table(name = "estudiantes", schema = "academico_schema")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia lógica al UUID del usuario en MS-Usuario.
     * Permite correlacionar identidad entre microservicios sin acoplamiento de BD.
     */
    @Column(name = "usuario_uuid", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID usuarioUuid;

    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false)
    private Integer curso;
}
