package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.domain.exception.UsuarioNoEncontradoException;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GestionUsuariosUseCase — Pruebas Unitarias")
class GestionUsuariosUseCaseImplTest {

    @Mock
    private UsuarioRepositoryPort repositoryPort;

    @InjectMocks
    private GestionUsuariosUseCaseImpl gestionUseCase;

    private Usuario usuario;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        usuario = new Usuario(
                "11222333-4", "docente@colegio.cl", "hash",
                RolUsuario.DOCENTE, "Carlos", "Rodriguez"
        );
    }

    @Test
    @DisplayName("obtenerPorId encuentra usuario existente")
    void obtenerPorId_existe_retornaUsuario() {
        when(repositoryPort.buscarPorId(id)).thenReturn(Optional.of(usuario));

        Usuario result = gestionUseCase.obtenerPorId(id);

        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo("11222333-4");
        assertThat(result.getEmail()).isEqualTo("docente@colegio.cl");
    }

    @Test
    @DisplayName("obtenerPorId no encuentra lanza UsuarioNoEncontradoException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(repositoryPort.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionUseCase.obtenerPorId(id))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    @DisplayName("obtenerPorRut encuentra usuario existente")
    void obtenerPorRut_existe_retornaUsuario() {
        when(repositoryPort.buscarPorRut("11222333-4")).thenReturn(Optional.of(usuario));

        Usuario result = gestionUseCase.obtenerPorRut("11222333-4");

        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo("11222333-4");
    }

    @Test
    @DisplayName("obtenerPorRut no encuentra lanza UsuarioNoEncontradoException")
    void obtenerPorRut_noExiste_lanzaExcepcion() {
        when(repositoryPort.buscarPorRut("99999999-9")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionUseCase.obtenerPorRut("99999999-9"))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    @DisplayName("listarPorRol retorna lista de usuarios")
    void listarPorRol_retornaLista() {
        when(repositoryPort.buscarPorRol("DOCENTE")).thenReturn(List.of(usuario));

        List<Usuario> result = gestionUseCase.listarPorRol("DOCENTE");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRut()).isEqualTo("11222333-4");
    }

    @Test
    @DisplayName("listarPorRol sin resultados retorna lista vacía")
    void listarPorRol_sinResultados_retornaVacia() {
        when(repositoryPort.buscarPorRol("ESTUDIANTE")).thenReturn(List.of());

        List<Usuario> result = gestionUseCase.listarPorRol("ESTUDIANTE");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("actualizar modifica nombre, apellido y email")
    void actualizar_existe_modificaDatos() {
        when(repositoryPort.buscarPorId(id)).thenReturn(Optional.of(usuario));
        when(repositoryPort.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario result = gestionUseCase.actualizar(id, "NuevoNombre", "NuevoApellido", "nuevo@email.cl");

        assertThat(result).isNotNull();
        assertThat(result.getNombreCompleto()).contains("NuevoNombre");
        verify(repositoryPort).guardar(any(Usuario.class));
    }

    @Test
    @DisplayName("actualizar usuario inexistente lanza UsuarioNoEncontradoException")
    void actualizar_noExiste_lanzaExcepcion() {
        when(repositoryPort.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionUseCase.actualizar(id, "X", "Y", "z@z.cl"))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    @DisplayName("eliminar desactiva usuario (soft delete)")
    void eliminar_existe_desactivaUsuario() {
        when(repositoryPort.buscarPorId(id)).thenReturn(Optional.of(usuario));
        when(repositoryPort.guardar(any(Usuario.class))).thenReturn(usuario);

        gestionUseCase.eliminar(id);

        assertThat(usuario.isActivo()).isFalse();
        verify(repositoryPort).guardar(usuario);
    }

    @Test
    @DisplayName("eliminar usuario inexistente lanza UsuarioNoEncontradoException")
    void eliminar_noExiste_lanzaExcepcion() {
        when(repositoryPort.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionUseCase.eliminar(id))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
