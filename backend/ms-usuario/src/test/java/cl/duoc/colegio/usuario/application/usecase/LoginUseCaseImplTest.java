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
                "docente@colegio.cl",
                "$2a$12$hashedPassword",
                RolUsuario.DOCENTE,
                "Carlos",
                "Rodríguez"
        );

        loginRequest = new LoginRequestDto("docente@colegio.cl", "password123");
    }

    @Test
    @DisplayName("Login exitoso retorna AuthResponseDto con token")
    void login_credencialesValidas_retornaToken() {
        // Arrange
        when(repositoryPort.buscarPorEmail("docente@colegio.cl"))
                .thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoderPort.matches("password123", "$2a$12$hashedPassword"))
                .thenReturn(true);
        when(strategyFactory.crear(RolUsuario.DOCENTE))
                .thenReturn(new DocenteAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any(Usuario.class)))
                .thenReturn("jwt.token.mock");

        // Act
        AuthResponseDto response = loginUseCase.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("jwt.token.mock");
        assertThat(response.tipo()).isEqualTo("Bearer");
        assertThat(response.email()).isEqualTo("docente@colegio.cl");
        assertThat(response.rol()).isEqualTo("DOCENTE");
        assertThat(response.permisos()).isNotEmpty();
    }

    @Test
    @DisplayName("Login con usuario inexistente lanza UsuarioNoEncontradoException")
    void login_usuarioNoExiste_lanzaExcepcion() {
        when(repositoryPort.buscarPorEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.login(loginRequest))
                .isInstanceOf(UsuarioNoEncontradoException.class);

        // Nunca debe llegar a verificar la contraseña
        verify(passwordEncoderPort, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Login con contraseña incorrecta lanza CredencialesInvalidasException")
    void login_passwordIncorrecto_lanzaExcepcion() {
        when(repositoryPort.buscarPorEmail("docente@colegio.cl"))
                .thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoderPort.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.login(loginRequest))
                .isInstanceOf(CredencialesInvalidasException.class);

        // Nunca debe generar token
        verify(tokenGeneratorPort, never()).generarToken(any());
    }

    @Test
    @DisplayName("Login con usuario inactivo lanza UsuarioInactivoException")
    void login_usuarioInactivo_lanzaExcepcion() {
        // Desactivar el usuario
        usuarioActivo.desactivar();

        when(repositoryPort.buscarPorEmail("docente@colegio.cl"))
                .thenReturn(Optional.of(usuarioActivo));

        assertThatThrownBy(() -> loginUseCase.login(loginRequest))
                .isInstanceOf(UsuarioInactivoException.class);

        // No debe verificar contraseña si el usuario está inactivo
        verify(passwordEncoderPort, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Login exitoso invoca el Factory y la Strategy correctamente")
    void login_exitoso_invocaFactoryYStrategy() {
        DocenteAuthorizationStrategy mockStrategy = spy(new DocenteAuthorizationStrategy());

        when(repositoryPort.buscarPorEmail("docente@colegio.cl"))
                .thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoderPort.matches(anyString(), anyString())).thenReturn(true);
        when(strategyFactory.crear(RolUsuario.DOCENTE)).thenReturn(mockStrategy);
        when(tokenGeneratorPort.generarToken(any())).thenReturn("token");

        loginUseCase.login(loginRequest);

        // Verificar que se invocó el factory con el rol correcto
        verify(strategyFactory).crear(RolUsuario.DOCENTE);
        // Verificar que se invocó la strategy
        verify(mockStrategy).resolverPermisos(any(Usuario.class));
    }

    @Test
    @DisplayName("El response incluye permisos de DOCENTE (notas, asistencias)")
    void login_rolDocente_responseContienePermisosDocente() {
        when(repositoryPort.buscarPorEmail("docente@colegio.cl"))
                .thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoderPort.matches(anyString(), anyString())).thenReturn(true);
        when(strategyFactory.crear(RolUsuario.DOCENTE))
                .thenReturn(new DocenteAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any())).thenReturn("token");

        AuthResponseDto response = loginUseCase.login(loginRequest);

        assertThat(response.permisos()).contains("notas", "asistencias");
    }
}
