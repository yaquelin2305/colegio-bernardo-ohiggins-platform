#set( $symbol_dollar = '$' )
package ${package}.application.strategy;

import java.util.List;

public class AdminAuthorizationStrategy implements AuthorizationStrategy {

    @Override
    public List<String> getRecursos() {
        return List.of("notas", "asistencias", "estudiantes", "docentes", "apoderados",
                "cursos", "reportes-academicos", "usuarios", "configuracion");
    }

    @Override
    public boolean isSoloLectura() {
        return false;
    }
}
