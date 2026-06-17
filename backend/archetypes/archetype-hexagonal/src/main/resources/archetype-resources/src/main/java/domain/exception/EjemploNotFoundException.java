#set( $symbol_dollar = '$' )
package ${package}.domain.exception;

/**
 * Excepción de dominio — no encontrado.
 */
public class EjemploNotFoundException extends RuntimeException {

    public EjemploNotFoundException(Long id) {
        super("Ejemplo no encontrado con ID: " + id);
    }
}
