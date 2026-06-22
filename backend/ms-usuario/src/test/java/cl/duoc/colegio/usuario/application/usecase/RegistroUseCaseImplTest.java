package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.domain.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.factory.UserStrategyFactory;
import cl.duoc.colegio.usuario.application.strategy.AdminAuthorizationStrategy;
import cl.duoc.colegio.usuario.application.strategy.ApoderadoAuthorizationStrategy;
import cl.duoc.colegio.usuario.application.strategy.DocenteAuthorizationStrategy;
import cl.duoc.colegio.usuario.application.strategy.EstudianteAuthorizationStrategy;
import cl.duoc.colegio.usuario.domain.exception.EmailYaRegistradoException;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.out.PasswordEncoderPort;
import cl.duoc.colegio.usuario.domain.port.out.TokenGeneratorPort;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistroUseCase — Pruebas Unitarias")
class RegistroUseCaseImplTest {

    @Mock
    private UsuarioRepositoryPort repositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenGeneratorPort tokenGeneratorPort;

    @Mock
    private UserStrategyFactory strategyFactory;

    @InjectMocks
    private RegistroUseCaseImpl registroUseCase;

    @Test
    @DisplayName("Registro DOCENTE exitoso retorna AuthResponseDto con token")
    void registroDocente_exitoso_retornaToken() {
        when(repositoryPort.existePorRut("11222333-4")).thenReturn(false);
        when(repositoryPort.existePorEmail("docente@test.cl")).thenReturn(false);
        when(passwordEncoderPort.encodear("password123")).thenReturn("$2a$12$hash");
        when(repositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(strategyFactory.crear(RolUsuario.DOCENTE)).thenReturn(new DocenteAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any(Usuario.class))).thenReturn("jwt.token.mocked");

        AuthResponseDto response = registroUseCase.registrar(
                "11222333-4", "docente@test.cl", "password123",
                "Juan", "Pérez", RolUsuario.DOCENTE, null, null
        );

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("jwt.token.mocked");
        assertThat(response.tipo()).isEqualTo("Bearer");
        assertThat(response.rut()).isEqualTo("11222333-4");
        assertThat(response.rol()).isEqualTo("DOCENTE");
        assertThat(response.permisos()).isNotEmpty();
        assertThat(response.permisos()).contains("notas", "asistencias");
        assertThat(response.expiraEn()).isGreaterThan(System.currentTimeMillis());
    }

    @Test
    @DisplayName("Registro ADMIN exitoso retorna permisos de admin")
    void registroAdmin_exitoso_retornaPermisosAdmin() {
        when(repositoryPort.existePorRut("12345678-9")).thenReturn(false);
        when(repositoryPort.existePorEmail("admin@colegio.cl")).thenReturn(false);
        when(passwordEncoderPort.encodear(anyString())).thenReturn("$2a$12$hash");
        when(repositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(strategyFactory.crear(RolUsuario.ADMIN)).thenReturn(new AdminAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any(Usuario.class))).thenReturn("admin.token");

        AuthResponseDto response = registroUseCase.registrar(
                "12345678-9", "admin@colegio.cl", "admin123!",
                "Admin", "Root", RolUsuario.ADMIN, null, null
        );

        assertThat(response).isNotNull();
        assertThat(response.rol()).isEqualTo("ADMIN");
        assertThat(response.permisos()).contains("usuarios", "configuracion");
    }

    @Test
    @DisplayName("Registro APODERADO con pupiloUuid asocia correctamente")
    void registroApoderado_conPupilo_asociaCorrectamente() {
        UUID pupiloId = UUID.randomUUID();

        when(repositoryPort.existePorRut("99888777-6")).thenReturn(false);
        when(repositoryPort.existePorEmail("apoderado@test.cl")).thenReturn(false);
        when(passwordEncoderPort.encodear(anyString())).thenReturn("$2a$12$hash");
        when(repositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(strategyFactory.crear(RolUsuario.APODERADO)).thenReturn(new ApoderadoAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any(Usuario.class))).thenReturn("token.apoderado");

        AuthResponseDto response = registroUseCase.registrar(
                "99888777-6", "apoderado@test.cl", "password",
                "María", "González", RolUsuario.APODERADO, 42L, pupiloId
        );

        assertThat(response).isNotNull();
        assertThat(response.rol()).isEqualTo("APODERADO");
    }

    @Test
    @DisplayName("Registro ESTUDIANTE exitoso")
    void registroEstudiante_exitoso() {
        when(repositoryPort.existePorRut("66555444-8")).thenReturn(false);
        when(repositoryPort.existePorEmail("alumno@test.cl")).thenReturn(false);
        when(passwordEncoderPort.encodear(anyString())).thenReturn("$2a$12$hash");
        when(repositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(strategyFactory.crear(RolUsuario.ESTUDIANTE)).thenReturn(new EstudianteAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any(Usuario.class))).thenReturn("token.estudiante");

        AuthResponseDto response = registroUseCase.registrar(
                "66555444-8", "alumno@test.cl", "password",
                "Pedro", "López", RolUsuario.ESTUDIANTE, 55L, null
        );

        assertThat(response).isNotNull();
        assertThat(response.rol()).isEqualTo("ESTUDIANTE");
        assertThat(response.permisos()).contains("notas", "asistencias");
    }

    @Test
    @DisplayName("RUT duplicado lanza EmailYaRegistradoException")
    void rutDuplicado_lanzaExcepcion() {
        when(repositoryPort.existePorRut("11222333-4")).thenReturn(true);

        assertThatThrownBy(() -> registroUseCase.registrar(
                "11222333-4", "docente@test.cl", "password",
                "Juan", "Pérez", RolUsuario.DOCENTE, null, null
        ))
                .isInstanceOf(EmailYaRegistradoException.class)
                .hasMessageContaining("RUT ya registrado");
    }

    @Test
    @DisplayName("Email duplicado lanza EmailYaRegistradoException")
    void emailDuplicado_lanzaExcepcion() {
        when(repositoryPort.existePorRut("11222333-4")).thenReturn(false);
        when(repositoryPort.existePorEmail("docente@test.cl")).thenReturn(true);

        assertThatThrownBy(() -> registroUseCase.registrar(
                "11222333-4", "docente@test.cl", "password",
                "Juan", "Pérez", RolUsuario.DOCENTE, null, null
        ))
                .isInstanceOf(EmailYaRegistradoException.class);
    }

    @Test
    @DisplayName("Verifica que se invoca encodear al registrar")
    void registro_invocaEncodear() {
        when(repositoryPort.existePorRut("11222333-4")).thenReturn(false);
        when(repositoryPort.existePorEmail("test@test.cl")).thenReturn(false);
        when(passwordEncoderPort.encodear("securePass")).thenReturn("hash");
        when(repositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(strategyFactory.crear(RolUsuario.DOCENTE)).thenReturn(new DocenteAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any())).thenReturn("token");

        registroUseCase.registrar(
                "11222333-4", "test@test.cl", "securePass",
                "Juan", "Pérez", RolUsuario.DOCENTE, null, null
        );

        verify(passwordEncoderPort).encodear("securePass");
    }

    @Test
    @DisplayName("Registro con perfilId=null y pupiloUuid=null no falla")
    void registro_sinPerfilNiPupilo_exitoso() {
        when(repositoryPort.existePorRut("11222333-4")).thenReturn(false);
        when(repositoryPort.existePorEmail("test@test.cl")).thenReturn(false);
        when(passwordEncoderPort.encodear(anyString())).thenReturn("hash");
        when(repositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(strategyFactory.crear(RolUsuario.DOCENTE)).thenReturn(new DocenteAuthorizationStrategy());
        when(tokenGeneratorPort.generarToken(any())).thenReturn("token");

        AuthResponseDto response = registroUseCase.registrar(
                "11222333-4", "test@test.cl", "password",
                "Juan", "Pérez", RolUsuario.DOCENTE, null, null
        );

        assertThat(response).isNotNull();
    }
}
