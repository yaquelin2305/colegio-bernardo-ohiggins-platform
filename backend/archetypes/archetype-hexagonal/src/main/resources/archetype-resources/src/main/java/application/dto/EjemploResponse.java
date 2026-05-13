#set( $symbol_dollar = '$' )
package ${package}.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EjemploResponse {

    private Long id;
    private String nombre;
    private String descripcion;

    public static EjemploResponse fromDomain(${package}.domain.model.Ejemplo ejemplo) {
        return EjemploResponse.builder()
                .id(ejemplo.getId())
                .nombre(ejemplo.getNombre())
                .descripcion(ejemplo.getDescripcion())
                .build();
    }
}
