#set( $symbol_dollar = '$' )
package ${package}.domain.port.out;

import ${package}.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> buscarPorRut(String rut);

    Optional<Usuario> buscarPorId(String id);

    Usuario guardar(Usuario usuario);
}
