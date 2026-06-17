#set( $symbol_dollar = '$' )
package ${package}.domain.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Entidad pura de dominio — sin anotaciones JPA ni Spring.
 */
@Getter
@Builder
public class Ejemplo {

    private final Long id;
    private final String nombre;
    private final String descripcion;

    public boolean esValido() {
        return nombre != null && !nombre.isBlank() && nombre.length() >= 3;
    }
}
