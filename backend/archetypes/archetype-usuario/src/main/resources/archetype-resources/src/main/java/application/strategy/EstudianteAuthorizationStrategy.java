#set( $symbol_dollar = '$' )
package ${package}.application.strategy;

import java.util.List;

public class EstudianteAuthorizationStrategy implements AuthorizationStrategy {

    @Override
    public List<String> getRecursos() {
        return List.of("notas", "asistencias");
    }

    @Override
    public boolean isSoloLectura() {
        return true; // Estudiante solo consulta
    }
}
