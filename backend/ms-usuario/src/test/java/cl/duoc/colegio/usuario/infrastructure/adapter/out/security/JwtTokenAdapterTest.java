package cl.duoc.colegio.usuario.infrastructure.adapter.out.security;

import cl.duoc.colegio.usuario.application.factory.UserStrategyFactory;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para JwtTokenAdapter.
 * Verifica generación y validación de tokens JWT.
 */
@DisplayName("JwtTokenAdapter — Pruebas Unitarias")
class JwtTokenAdapterTest {

    private JwtTokenAdapter jwtTokenAdapter;
    private static final String SECRET =
            "colegio-bernardo-ohiggins-secret-key-2024-duoc-fs3-very-long-secret";

    @BeforeEach
    void setUp() {
        jwtTokenAdapter = new JwtTokenAdapter(SECRET, new UserStrategyFactory());
    }

    @Test
    @DisplayName("Genera token no nulo y no vacío")
    void generarToken_retornaTokenValido() {
        Usuario usuario = crearUsuario(RolUsuario.DOCENTE);
        String token = jwtTokenAdapter.generarToken(usuario);
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("Token generado es válido para validación")
    void generarToken_tokenEsValidable() {
        Usuario usuario = crearUsuario(RolUsuario.DOCENTE);
        String token = jwtTokenAdapter.generarToken(usuario);
        assertThat(jwtTokenAdapter.validarToken(token)).isTrue();
    }

    @Test
    @DisplayName("Extrae el email del subject del token")
    void extraerEmail_retornaEmailCorrecto() {
        Usuario usuario = crearUsuario(RolUsuario.APODERADO);
        String token = jwtTokenAdapter.generarToken(usuario);
        assertThat(jwtTokenAdapter.extraerEmail(token)).isEqualTo("test@colegio.cl");
    }

    @Test
    @DisplayName("Token inválido (string random) retorna false en validación")
    void validarToken_tokenInvalido_retornaFalse() {
        assertThat(jwtTokenAdapter.validarToken("esto.no.es.un.jwt")).isFalse();
    }

    @Test
    @DisplayName("Token vacío retorna false en validación")
    void validarToken_tokenVacio_retornaFalse() {
        assertThat(jwtTokenAdapter.validarToken("")).isFalse();
    }

    @Test
    @DisplayName("Genera tokens distintos para distintos usuarios")
    void generarToken_distintoUsuarios_retornaTokensDiferentes() {
        Usuario u1 = crearUsuario(RolUsuario.DOCENTE);
        Usuario u2 = new Usuario("otro@colegio.cl", "hash", RolUsuario.APODERADO, "Otra", "Persona");
        assertThat(jwtTokenAdapter.generarToken(u1))
                .isNotEqualTo(jwtTokenAdapter.generarToken(u2));
    }

    private Usuario crearUsuario(RolUsuario rol) {
        Usuario u = new Usuario("test@colegio.cl", "hash", rol, "Test", "Usuario");
        u.asociarPerfil(1L);
        return u;
    }
}
