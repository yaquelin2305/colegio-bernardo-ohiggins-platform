#set( $symbol_dollar = '$' )
package ${package}.application.strategy;

import java.util.List;

public class ApoderadoAuthorizationStrategy implements AuthorizationStrategy {

    @Override
    public List<String> getRecursos() {
        return List.of("notas", "asistencias", "reportes-academicos");
    }

    @Override
    public boolean isSoloLectura() {
        return true; // Apoderado no modifica datos
    }
}
