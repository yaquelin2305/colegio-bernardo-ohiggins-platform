#set( $symbol_dollar = '$' )
package ${package}.application.strategy;

import java.util.List;

/**
 * Patrón Strategy — cada rol define sus propios recursos.
 */
public interface AuthorizationStrategy {

    List<String> getRecursos();

    boolean isSoloLectura();
}
