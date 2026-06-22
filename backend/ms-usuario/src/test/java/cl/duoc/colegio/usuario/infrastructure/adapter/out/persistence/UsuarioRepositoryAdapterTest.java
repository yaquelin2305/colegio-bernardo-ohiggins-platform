package cl.duoc.colegio.usuario.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioRepositoryAdapter — Pruebas Unitarias")
class UsuarioRepositoryAdapterTest {

    @Mock
    private UsuarioJpaRepository jpaRepository;

    @InjectMocks
    private UsuarioRepositoryAdapter adapter;

    private UsuarioEntity entity;
    private Usuario domain;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        entity = new UsuarioEntity();
        entity.setId(id);
        entity.setRut("11222333-4");
        entity.setEmail("docente@colegio.cl");
        entity.setPasswordHash("hash");
        entity.setRol("DOCENTE");
        entity.setNombre("Carlos");
        entity.setApellido("Rodriguez");
        entity.setPerfilId(42L);
        entity.setPupiloUuid(null);
        entity.setActivo(true);
        entity.setCreadoEn(LocalDateTime.now());
        entity.setActualizadoEn(LocalDateTime.now());

        domain = new Usuario(
                id, "11222333-4", "docente@colegio.cl", "hash",
                RolUsuario.DOCENTE, "Carlos", "Rodriguez",
                42L, null, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("buscarPorRut encuentra usuario")
    void buscarPorRut_existe_retornaDominio() {
        when(jpaRepository.findByRut("11222333-4")).thenReturn(Optional.of(entity));

        Optional<Usuario> result = adapter.buscarPorRut("11222333-4");

        assertThat(result).isPresent();
        assertThat(result.get().getRut()).isEqualTo("11222333-4");
        assertThat(result.get().getEmail()).isEqualTo("docente@colegio.cl");
        assertThat(result.get().getRol()).isEqualTo(RolUsuario.DOCENTE);
    }

    @Test
    @DisplayName("buscarPorRut no encuentra retorna empty")
    void buscarPorRut_noExiste_retornaEmpty() {
        when(jpaRepository.findByRut("99999999-9")).thenReturn(Optional.empty());

        Optional<Usuario> result = adapter.buscarPorRut("99999999-9");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("buscarPorEmail encuentra usuario")
    void buscarPorEmail_existe_retornaDominio() {
        when(jpaRepository.findByEmail("docente@colegio.cl")).thenReturn(Optional.of(entity));

        Optional<Usuario> result = adapter.buscarPorEmail("docente@colegio.cl");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("docente@colegio.cl");
    }

    @Test
    @DisplayName("buscarPorEmail no encuentra retorna empty")
    void buscarPorEmail_noExiste_retornaEmpty() {
        when(jpaRepository.findByEmail("no@existe.cl")).thenReturn(Optional.empty());

        Optional<Usuario> result = adapter.buscarPorEmail("no@existe.cl");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId encuentra usuario")
    void buscarPorId_existe_retornaDominio() {
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<Usuario> result = adapter.buscarPorId(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("buscarPorId no encuentra retorna empty")
    void buscarPorId_noExiste_retornaEmpty() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Usuario> result = adapter.buscarPorId(id);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("guardar persiste y retorna dominio")
    void guardar_persisteYRetorna() {
        when(jpaRepository.save(any(UsuarioEntity.class))).thenReturn(entity);

        Usuario result = adapter.guardar(domain);

        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo("11222333-4");
    }

    @Test
    @DisplayName("existePorRut retorna true si existe")
    void existePorRut_existe_retornaTrue() {
        when(jpaRepository.existsByRut("11222333-4")).thenReturn(true);

        assertThat(adapter.existePorRut("11222333-4")).isTrue();
    }

    @Test
    @DisplayName("existePorRut retorna false si no existe")
    void existePorRut_noExiste_retornaFalse() {
        when(jpaRepository.existsByRut("99999999-9")).thenReturn(false);

        assertThat(adapter.existePorRut("99999999-9")).isFalse();
    }

    @Test
    @DisplayName("existePorEmail retorna true si existe")
    void existePorEmail_existe_retornaTrue() {
        when(jpaRepository.existsByEmail("docente@colegio.cl")).thenReturn(true);

        assertThat(adapter.existePorEmail("docente@colegio.cl")).isTrue();
    }

    @Test
    @DisplayName("existePorEmail retorna false si no existe")
    void existePorEmail_noExiste_retornaFalse() {
        when(jpaRepository.existsByEmail("no@existe.cl")).thenReturn(false);

        assertThat(adapter.existePorEmail("no@existe.cl")).isFalse();
    }

    @Test
    @DisplayName("buscarPorRol retorna lista de usuarios")
    void buscarPorRol_retornaLista() {
        when(jpaRepository.findByRol("DOCENTE")).thenReturn(List.of(entity));

        List<Usuario> result = adapter.buscarPorRol("DOCENTE");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRut()).isEqualTo("11222333-4");
        assertThat(result.get(0).getRol()).isEqualTo(RolUsuario.DOCENTE);
    }

    @Test
    @DisplayName("buscarPorRol sin resultados retorna lista vacía")
    void buscarPorRol_sinResultados_retornaVacia() {
        when(jpaRepository.findByRol("ESTUDIANTE")).thenReturn(List.of());

        List<Usuario> result = adapter.buscarPorRol("ESTUDIANTE");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("toDomain mapea correctamente perfilId y pupiloUuid")
    void toDomain_conPerfilYPupilo_mapeaBien() {
        UUID pupilo = UUID.randomUUID();
        entity.setPupiloUuid(pupilo);
        entity.setPerfilId(10L);

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<Usuario> result = adapter.buscarPorId(id);

        assertThat(result).isPresent();
        assertThat(result.get().getPerfilId()).isEqualTo(10L);
        assertThat(result.get().getPupiloUuid()).isEqualTo(pupilo);
    }

    @Test
    @DisplayName("toDomain con APODERADO mapea rol correctamente")
    void toDomain_apoderado_mapeaRol() {
        entity.setRol("APODERADO");
        entity.setEmail("apoderado@test.cl");
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<Usuario> result = adapter.buscarPorId(id);

        assertThat(result).isPresent();
        assertThat(result.get().getRol()).isEqualTo(RolUsuario.APODERADO);
    }
}
