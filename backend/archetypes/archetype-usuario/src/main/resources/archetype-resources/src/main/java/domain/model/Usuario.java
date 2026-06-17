#set( $symbol_dollar = '$' )
package ${package}.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Usuario {

    private final String id;
    private final String rut;
    private final String email;
    private final String passwordHash;
    private final String nombre;
    private final String apellido;
    private final RolUsuario rol;
    private final boolean activo;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public List<String> getRecursos() {
        return rol.getRecursos();
    }
}
