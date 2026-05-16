#set( $symbol_dollar = '$' )
package ${package}.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA — separada del modelo de dominio.
 * El mapeo domain ↔ JPA ocurre en el adaptador, no en el dominio.
 */
@Entity
@Table(name = "ejemplos")
@Getter
@Setter
@NoArgsConstructor
public class EjemploEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    public static EjemploEntity fromDomain(${package}.domain.model.Ejemplo ejemplo) {
        EjemploEntity entity = new EjemploEntity();
        entity.setId(ejemplo.getId());
        entity.setNombre(ejemplo.getNombre());
        entity.setDescripcion(ejemplo.getDescripcion());
        return entity;
    }

    public ${package}.domain.model.Ejemplo toDomain() {
        return ${package}.domain.model.Ejemplo.builder()
                .id(this.id)
                .nombre(this.nombre)
                .descripcion(this.descripcion)
                .build();
    }
}
