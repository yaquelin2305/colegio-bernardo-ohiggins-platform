#set( $symbol_dollar = '$' )
package ${package}.domain.port.in;

import ${package}.domain.model.Ejemplo;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada — caso de uso consultar/crear Ejemplo.
 */
public interface EjemploUseCase {

    Optional<Ejemplo> obtenerPorId(Long id);

    List<Ejemplo> listarTodos();

    Ejemplo crear(Ejemplo ejemplo);
}
