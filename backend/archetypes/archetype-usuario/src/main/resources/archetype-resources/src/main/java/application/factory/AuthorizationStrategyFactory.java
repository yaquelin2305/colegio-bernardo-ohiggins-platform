#set( $symbol_dollar = '$' )
package ${package}.application.factory;

import ${package}.application.strategy.*;
import ${package}.domain.model.RolUsuario;
import org.springframework.stereotype.Component;

/**
 * Patrón Factory Method — centraliza la creación de la Strategy según el rol.
 * Ventajas:
 *   - Open/Closed: nuevo rol = nueva Strategy, sin modificar código existente
 *   - Desacopla LoginUseCase de las implementaciones concretas
 *   - Testabilidad: cada Strategy se prueba aislada
 */
@Component
public class AuthorizationStrategyFactory {

    public AuthorizationStrategy crear(RolUsuario rol) {
        return switch (rol) {
            case ADMIN      -> new AdminAuthorizationStrategy();
            case DOCENTE    -> new DocenteAuthorizationStrategy();
            case APODERADO  -> new ApoderadoAuthorizationStrategy();
            case ESTUDIANTE -> new EstudianteAuthorizationStrategy();
        };
    }
}
