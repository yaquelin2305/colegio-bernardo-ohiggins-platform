package cl.duoc.colegio.usuario.application.strategy;

import cl.duoc.colegio.usuario.application.factory.UserStrategyFactory;
import cl.duoc.colegio.usuario.domain.exception.RolNoSoportadoException;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas unitarias para el Factory Method.
 * Verifica que la fábrica retorne la instancia correcta para cada rol.
 */
@DisplayName("UserStrategyFactory — Pruebas Unitarias")
class UserStrategyFactoryTest {

    private UserStrategyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new UserStrategyFactory();
    }

    @Test
    @DisplayName("Crea DocenteStrategy para rol DOCENTE")
    void crear_rolDocente_retornaDocenteStrategy() {
        AuthorizationStrategy strategy = factory.crear(RolUsuario.DOCENTE);
        assertThat(strategy).isInstanceOf(DocenteAuthorizationStrategy.class);
    }

    @Test
    @DisplayName("Crea ApoderadoStrategy para rol APODERADO")
    void crear_rolApoderado_retornaApoderadoStrategy() {
        AuthorizationStrategy strategy = factory.crear(RolUsuario.APODERADO);
        assertThat(strategy).isInstanceOf(ApoderadoAuthorizationStrategy.class);
    }

    @Test
    @DisplayName("Crea EstudianteStrategy para rol ESTUDIANTE")
    void crear_rolEstudiante_retornaEstudianteStrategy() {
        AuthorizationStrategy strategy = factory.crear(RolUsuario.ESTUDIANTE);
        assertThat(strategy).isInstanceOf(EstudianteAuthorizationStrategy.class);
    }

    @Test
    @DisplayName("Crea AdminStrategy para rol ADMIN")
    void crear_rolAdmin_retornaAdminStrategy() {
        AuthorizationStrategy strategy = factory.crear(RolUsuario.ADMIN);
        assertThat(strategy).isInstanceOf(AdminAuthorizationStrategy.class);
    }

    @ParameterizedTest
    @EnumSource(RolUsuario.class)
    @DisplayName("Todos los roles tienen estrategia registrada")
    void crear_todoLosRoles_noLanzaExcepcion(RolUsuario rol) {
        // No debe lanzar excepción para ningún rol definido
        AuthorizationStrategy strategy = factory.crear(rol);
        assertThat(strategy).isNotNull();
    }

    @Test
    @DisplayName("Cada llamada retorna una nueva instancia (no singleton)")
    void crear_llamadasMultiples_retornaNuevasInstancias() {
        AuthorizationStrategy s1 = factory.crear(RolUsuario.DOCENTE);
        AuthorizationStrategy s2 = factory.crear(RolUsuario.DOCENTE);
        // Cada llamada debe crear una nueva instancia
        assertThat(s1).isNotSameAs(s2);
    }
}
