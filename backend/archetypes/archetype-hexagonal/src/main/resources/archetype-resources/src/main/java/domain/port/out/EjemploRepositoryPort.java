#set( $symbol_dollar = '$' )
package ${package}.domain.port.out;

import ${package}.domain.model.Ejemplo;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida — contrato de persistencia para Ejemplo.
 */
public interface EjemploRepositoryPort {

    Optional<Ejemplo> findById(Long id);

    List<Ejemplo> findAll();

    Ejemplo save(Ejemplo ejemplo);

    void deleteById(Long id);
}
