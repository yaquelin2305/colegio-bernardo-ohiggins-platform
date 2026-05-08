package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.dto.LoginRequestDto;
import cl.duoc.colegio.usuario.application.factory.UserStrategyFactory;
import cl.duoc.colegio.usuario.application.strategy.DocenteAuthorizationStrategy;
import cl.duoc.colegio.usuario.domain.exception.CredencialesInvalidasException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioInactivoException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioNoEncontradoException;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.out.PasswordEncoderPort;
import cl.duoc.colegio.usuario.domain.port.out.TokenGeneratorPort;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para LoginUseCaseImpl.
 * Usa Mockito para aislar completamente el caso de uso de sus dependencias.
 * Login se hace por RUT (formato chileno), no por email.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUseCase — Pruebas Unitarias")
class LoginUseCaseImplTest {

    @Mock
    private UsuarioRepositoryPort repositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenGeneratorPort tokenGeneratorPort;

    @Mock
    private UserStrategyFactory strategyFactory;

    @InjectMocks
    private LoginUseCaseImpl loginUseCase;

    private Usuario usuarioActivo;
    private LoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {
        usuarioActivo = new Usuario(
                "11222333-4",
                "docente@colegio.cl",
                "$2a$12$hashedPassword",
                RolUsuario.DOCENTE,
                "Carlos",
                "Rodríguez"
        );

        loginRequest = new LoginRequestDto("11222333-4", "password123");
    }

    @Test
    @DisplayName("Login exitoso retorna AuthResponseDto con token")
    void login_credencialesValidas_retornaToken() {
        when(repositoryPort.buscarPorRut("11222333-4"))
                .thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoderPort.matches("password123", "$2a$12$hashedPassword"))
                .thenReturn(true);
        when(strategyFactory.crear(RolUsuario.DOCENTE))
                .thenReturn(new DocenteAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any(Usuario.class)))
                .thenReturn("jwt.token.mock");

        AuthResponseDto response = loginUseCase.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("jwt.token.mock");
        assertThat(response.tipo()).isEqualTo("Bearer");
        assertThat(response.rut()).isEqualTo("11222333-4");
        assertThat(response.rol()).isEqualTo("DOCENTE");
        assertThat(response.permisos()).isNotEmpty();
    }

    @Test
    @DisplayName("Login con usuario inexistente lanza UsuarioNoEncontradoException")
    void login_usuarioNoExiste_lanzaExcepcion() {
        when(repositoryPort.buscarPorRut(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.login(loginRequest))
                .isInstanceOf(UsuarioNoEncontradoException.class);

        verify(passwordEncoderPort, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Login con contraseña incorrecta lanza CredencialesInvalidasException")
    void login_passwordIncorrecto_lanzaExcepcion() {
        when(repositoryPort.buscarPorRut("11222333-4"))
                .thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoderPort.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.login(loginRequest))
                .isInstanceOf(CredencialesInvalidasException.class);

        verify(tokenGeneratorPort, never()).generarToken(any());
    }

    @Test
    @DisplayName("Login con usuario inactivo lanza UsuarioInactivoException")
    void login_usuarioInactivo_lanzaExcepcion() {
        usuarioActivo.desactivar();

        when(repositoryPort.buscarPorRut("11222333-4"))
                .thenReturn(Optional.of(usuarioActivo));

        assertThatThrownBy(() -> loginUseCase.login(loginRequest))
                .isInstanceOf(UsuarioInactivoException.class);

        verify(passwordEncoderPort, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Login exitoso invoca el Factory y la Strategy correctamente")
    void login_exitoso_invocaFactoryYStrategy() {
        DocenteAuthorizationStrategy mockStrategy = spy(new DocenteAuthorizationStrategy());

        when(repositoryPort.buscarPorRut("11222333-4"))
                .thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoderPort.matches(anyString(), anyString())).thenReturn(true);
        when(strategyFactory.crear(RolUsuario.DOCENTE)).thenReturn(mockStrategy);
        when(tokenGeneratorPort.generarToken(any())).thenReturn("token");

        loginUseCase.login(loginRequest);

        verify(strategyFactory).crear(RolUsuario.DOCENTE);
        verify(mockStrategy).resolverPermisos(any(Usuario.class));
    }

    @Test
    @DisplayName("El response incluye permisos de DOCENTE (notas, asistencias)")
    void login_rolDocente_responseContienePermisosDocente() {
        when(repositoryPort.buscarPorRut("11222333-4"))
                .thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoderPort.matches(anyString(), anyString())).thenReturn(true);
        when(strategyFactory.crear(RolUsuario.DOCENTE))
                .thenReturn(new DocenteAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any())).thenReturn("token");

        AuthResponseDto response = loginUseCase.login(loginRequest);

        assertThat(response.permisos()).contains("notas", "asistencias");
    }
}
