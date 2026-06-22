package cl.duoc.colegio.usuario.infrastructure.adapter.in.rest;

import cl.duoc.colegio.usuario.application.dto.NombreDto;
import cl.duoc.colegio.usuario.domain.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.in.GestionUsuariosUseCase;
import cl.duoc.colegio.usuario.domain.port.in.LoginUseCase;
import cl.duoc.colegio.usuario.domain.port.in.RegistroUseCase;
import cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto.ActualizarUsuarioRequestDto;
import cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto.LoginRequestDto;
import cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto.RegistroRequestDto;
import cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto.UsuarioResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController — Pruebas Unitarias")
class AuthControllerTest {

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private RegistroUseCase registroUseCase;

    @Mock
    private GestionUsuariosUseCase gestionUseCase;

    @InjectMocks
    private AuthController controller;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(
                "11222333-4", "docente@colegio.cl", "hash",
                RolUsuario.DOCENTE, "Carlos", "Rodriguez"
        );
    }

    @Test
    @DisplayName("POST /login retorna 200 con AuthResponseDto")
    void login_exitoso_retorna200() {
        LoginRequestDto request = new LoginRequestDto("11222333-4", "password");
        AuthResponseDto authResponse = AuthResponseDto.of(
                "token123", "11222333-4", "Carlos Rodriguez",
                "DOCENTE", List.of("notas", "asistencias"),
                System.currentTimeMillis() + 86400000L
        );
        when(loginUseCase.login("11222333-4", "password")).thenReturn(authResponse);

        ResponseEntity<AuthResponseDto> response = controller.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isEqualTo("token123");
        assertThat(response.getBody().rut()).isEqualTo("11222333-4");
    }

    @Test
    @DisplayName("GET /health retorna mensaje operativo")
    void health_retornaMensaje() {
        ResponseEntity<String> response = controller.health();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("MS-Usuario operativo");
    }

    @Test
    @DisplayName("GET /usuarios/{uuid}/nombre retorna NombreDto")
    void obtenerNombre_existe_retornaNombreDto() {
        UUID id = usuario.getId();
        when(gestionUseCase.obtenerPorId(id)).thenReturn(usuario);

        ResponseEntity<NombreDto> response = controller.obtenerNombre(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().nombreCompleto()).isEqualTo("Carlos Rodriguez");
    }

    @Test
    @DisplayName("POST /admin/crear retorna 201 con UsuarioResponseDto")
    void crear_exitoso_retorna201() {
        RegistroRequestDto request = new RegistroRequestDto(
                "11222333-4", "docente@colegio.cl", "password123",
                "Carlos", "Rodriguez", RolUsuario.DOCENTE, null, null
        );
        when(gestionUseCase.obtenerPorRut("11222333-4")).thenReturn(usuario);

        ResponseEntity<UsuarioResponseDto> response = controller.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().rut()).isEqualTo("11222333-4");
        assertThat(response.getBody().email()).isEqualTo("docente@colegio.cl");
        verify(registroUseCase).registrar(
                "11222333-4", "docente@colegio.cl", "password123",
                "Carlos", "Rodriguez", RolUsuario.DOCENTE, null, null
        );
    }

    @Test
    @DisplayName("GET /admin/{id} retorna UsuarioResponseDto")
    void obtenerPorId_existe_retorna200() {
        UUID id = usuario.getId();
        when(gestionUseCase.obtenerPorId(id)).thenReturn(usuario);

        ResponseEntity<UsuarioResponseDto> response = controller.obtenerPorId(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(id);
    }

    @Test
    @DisplayName("GET /admin/listar/{rol} retorna lista de UsuarioResponseDto")
    void listarPorRol_retornaLista() {
        when(gestionUseCase.listarPorRol("DOCENTE")).thenReturn(List.of(usuario));

        ResponseEntity<List<UsuarioResponseDto>> response = controller.listarPorRol("DOCENTE");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).rut()).isEqualTo("11222333-4");
    }

    @Test
    @DisplayName("PUT /admin/actualizar/{id} retorna UsuarioResponseDto actualizado")
    void actualizar_exitoso_retorna200() {
        UUID id = usuario.getId();
        ActualizarUsuarioRequestDto request = new ActualizarUsuarioRequestDto(
                "Nuevo", "Nombre", "nuevo@email.cl"
        );
        when(gestionUseCase.actualizar(id, "Nuevo", "Nombre", "nuevo@email.cl"))
                .thenReturn(usuario);

        ResponseEntity<UsuarioResponseDto> response = controller.actualizar(id, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("DELETE /admin/eliminar/{id} retorna 204 No Content")
    void eliminar_exitoso_retorna204() {
        UUID id = usuario.getId();

        ResponseEntity<Void> response = controller.eliminar(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(gestionUseCase).eliminar(id);
    }
}
