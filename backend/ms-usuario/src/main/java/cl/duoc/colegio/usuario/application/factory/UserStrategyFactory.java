package cl.duoc.colegio.usuario.application.factory;

import cl.duoc.colegio.usuario.application.strategy.AdminAuthorizationStrategy;
import cl.duoc.colegio.usuario.application.strategy.ApoderadoAuthorizationStrategy;
import cl.duoc.colegio.usuario.application.strategy.AuthorizationStrategy;
import cl.duoc.colegio.usuario.application.strategy.DocenteAuthorizationStrategy;
import cl.duoc.colegio.usuario.application.strategy.EstudianteAuthorizationStrategy;
import cl.duoc.colegio.usuario.domain.exception.RolNoSoportadoException;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import org.springframework.stereotype.Component;

/**
 * Factory Method para instanciar la estrategia de autorización correcta.
 *
 * ¿Por qué Factory Method?
 * Porque el cliente (LoginUseCase) no debe conocer las clases concretas
 * de cada Strategy. El Factory centraliza la lógica de creación y
 * garantiza que siempre se retorne la estrategia correcta para cada rol.
 *
 * Si mañana existe un rol nuevo (ej: INSPECTOR), solo se agrega aquí
 * y se crea su Strategy. El resto del código NO cambia.
 *
 * Principio aplicado: Open/Closed + Single Responsibility.
 */
@Component
public class UserStrategyFactory {

    /**
     * Crea y retorna la estrategia de autorización correspondiente al rol.
     *
     * @param rol el rol del usuario autenticado
     * @return la estrategia concreta para ese rol
     * @throws RolNoSoportadoException si el rol no tiene estrategia registrada
     */
    public AuthorizationStrategy crear(RolUsuario rol) {
        return switch (rol) {
            case DOCENTE    -> new DocenteAuthorizationStrategy();
            case APODERADO  -> new ApoderadoAuthorizationStrategy();
            case ESTUDIANTE -> new EstudianteAuthorizationStrategy();
            case ADMIN      -> new AdminAuthorizationStrategy();
            default         -> throw new RolNoSoportadoException(rol.name());
        };
    }
}
