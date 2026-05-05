package cl.duoc.colegio.usuario.application.strategy;

import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para el Patrón Strategy de Autorización.
 *
 * Valida que cada rol retorna exactamente los permisos correctos
 * y que los claims del JWT contienen la información necesaria.
 */
@DisplayName("Authorization Strategy — Pruebas Unitarias")
class AuthorizationStrategyTest {

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = new Usuario(
                "test@colegio.cl",
                "hash",
                RolUsuario.DOCENTE,
                "Juan",
                "Pérez"
        );
        usuarioBase.asociarPerfil(42L);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DOCENTE
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("DocenteAuthorizationStrategy")
    class DocenteStrategyTest {

        private DocenteAuthorizationStrategy strategy;

        @BeforeEach
        void setUp() {
            strategy = new DocenteAuthorizationStrategy();
        }

        @Test
        @DisplayName("El docente NO es solo lectura")
        void docente_noEsSoloLectura() {
            Permisos permisos = strategy.resolverPermisos(usuarioBase);
            assertThat(permisos.isSoloLectura()).isFalse();
        }

        @Test
        @DisplayName("El docente puede acceder a notas y asistencias")
        void docente_puedeAccederANotasYAsistencias() {
            Permisos permisos = strategy.resolverPermisos(usuarioBase);
            assertThat(permisos.puedeAcceder("notas")).isTrue();
            assertThat(permisos.puedeAcceder("asistencias")).isTrue();
        }

        @Test
        @DisplayName("El docente puede ejecutar GET, POST y PUT")
        void docente_puedeEjecutarGetPostPut() {
            Permisos permisos = strategy.resolverPermisos(usuarioBase);
            assertThat(permisos.puedeEjecutar("GET")).isTrue();
            assertThat(permisos.puedeEjecutar("POST")).isTrue();
            assertThat(permisos.puedeEjecutar("PUT")).isTrue();
        }

        @Test
        @DisplayName("El docente NO puede ejecutar DELETE")
        void docente_noPuedeEjecutarDelete() {
            Permisos permisos = strategy.resolverPermisos(usuarioBase);
            assertThat(permisos.puedeEjecutar("DELETE")).isFalse();
        }

        @Test
        @DisplayName("Los claims del docente incluyen el rol correcto")
        void docente_claimsContienenRol() {
            Map<String, Object> claims = strategy.generarClaimsAdicionales(usuarioBase);
            assertThat(claims.get("rol")).isEqualTo("DOCENTE");
            assertThat(claims.get("soloLectura")).isEqualTo(false);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // APODERADO
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("ApoderadoAuthorizationStrategy")
    class ApoderadoStrategyTest {

        private ApoderadoAuthorizationStrategy strategy;
        private Usuario apoderado;

        @BeforeEach
        void setUp() {
            strategy = new ApoderadoAuthorizationStrategy();
            apoderado = new Usuario("apoderado@test.cl", "hash", RolUsuario.APODERADO, "María", "González");
            apoderado.asociarPerfil(99L);
        }

        @Test
        @DisplayName("El apoderado es SOLO lectura")
        void apoderado_esSoloLectura() {
            Permisos permisos = strategy.resolverPermisos(apoderado);
            assertThat(permisos.isSoloLectura()).isTrue();
        }

        @Test
        @DisplayName("El apoderado NO puede ejecutar POST, PUT ni DELETE")
        void apoderado_noTienePermisoDeEscritura() {
            Permisos permisos = strategy.resolverPermisos(apoderado);
            assertThat(permisos.puedeEjecutar("POST")).isFalse();
            assertThat(permisos.puedeEjecutar("PUT")).isFalse();
            assertThat(permisos.puedeEjecutar("DELETE")).isFalse();
        }

        @Test
        @DisplayName("El apoderado NO puede acceder a datos de configuración")
        void apoderado_noAccedeAConfiguracion() {
            Permisos permisos = strategy.resolverPermisos(apoderado);
            assertThat(permisos.puedeAcceder("configuracion")).isFalse();
            assertThat(permisos.puedeAcceder("usuarios")).isFalse();
        }

        @Test
        @DisplayName("Los claims del apoderado incluyen el pupiloId para filtrado")
        void apoderado_claimsContienenPupiloId() {
            Map<String, Object> claims = strategy.generarClaimsAdicionales(apoderado);
            assertThat(claims.get("rol")).isEqualTo("APODERADO");
            assertThat(claims.get("pupiloId")).isEqualTo(99L);
            assertThat(claims.get("soloLectura")).isEqualTo(true);
        }

        @Test
        @DisplayName("El pupiloId es 0 cuando el apoderado no tiene perfil asociado")
        void apoderado_sinPerfil_pupiloIdEsCero() {
            Usuario sinPerfil = new Usuario("sin@perfil.cl", "hash", RolUsuario.APODERADO, "Sin", "Perfil");
            Map<String, Object> claims = strategy.generarClaimsAdicionales(sinPerfil);
            assertThat(claims.get("pupiloId")).isEqualTo(0L);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ESTUDIANTE
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("EstudianteAuthorizationStrategy")
    class EstudianteStrategyTest {

        private EstudianteAuthorizationStrategy strategy;
        private Usuario estudiante;

        @BeforeEach
        void setUp() {
            strategy = new EstudianteAuthorizationStrategy();
            estudiante = new Usuario("alumno@test.cl", "hash", RolUsuario.ESTUDIANTE, "Pedro", "López");
            estudiante.asociarPerfil(55L);
        }

        @Test
        @DisplayName("El estudiante es SOLO lectura")
        void estudiante_esSoloLectura() {
            assertThat(strategy.resolverPermisos(estudiante).isSoloLectura()).isTrue();
        }

        @Test
        @DisplayName("El estudiante solo accede a notas y asistencias")
        void estudiante_soloAccedeANotasYAsistencias() {
            Permisos permisos = strategy.resolverPermisos(estudiante);
            assertThat(permisos.puedeAcceder("notas")).isTrue();
            assertThat(permisos.puedeAcceder("asistencias")).isTrue();
            assertThat(permisos.puedeAcceder("estudiantes")).isFalse();
            assertThat(permisos.puedeAcceder("docentes")).isFalse();
        }

        @Test
        @DisplayName("Los claims del estudiante incluyen el estudianteId")
        void estudiante_claimsContienenEstudianteId() {
            Map<String, Object> claims = strategy.generarClaimsAdicionales(estudiante);
            assertThat(claims.get("rol")).isEqualTo("ESTUDIANTE");
            assertThat(claims.get("estudianteId")).isEqualTo(55L);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ADMIN
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("AdminAuthorizationStrategy")
    class AdminStrategyTest {

        private AdminAuthorizationStrategy strategy;
        private Usuario admin;

        @BeforeEach
        void setUp() {
            strategy = new AdminAuthorizationStrategy();
            admin = new Usuario("admin@colegio.cl", "hash", RolUsuario.ADMIN, "Super", "Admin");
        }

        @Test
        @DisplayName("El admin NO es solo lectura")
        void admin_noEsSoloLectura() {
            assertThat(strategy.resolverPermisos(admin).isSoloLectura()).isFalse();
        }

        @Test
        @DisplayName("El admin puede acceder a todos los recursos")
        void admin_puedeAccederATodo() {
            Permisos permisos = strategy.resolverPermisos(admin);
            assertThat(permisos.puedeAcceder("notas")).isTrue();
            assertThat(permisos.puedeAcceder("usuarios")).isTrue();
            assertThat(permisos.puedeAcceder("configuracion")).isTrue();
        }

        @Test
        @DisplayName("El admin puede ejecutar todas las operaciones incluyendo DELETE")
        void admin_puedeEjecutarTodo() {
            Permisos permisos = strategy.resolverPermisos(admin);
            assertThat(permisos.puedeEjecutar("DELETE")).isTrue();
        }
    }
}
