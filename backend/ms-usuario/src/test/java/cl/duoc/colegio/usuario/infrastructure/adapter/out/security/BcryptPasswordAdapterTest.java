package cl.duoc.colegio.usuario.infrastructure.adapter.out.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BcryptPasswordAdapter — Pruebas Unitarias")
class BcryptPasswordAdapterTest {

    private BcryptPasswordAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BcryptPasswordAdapter();
    }

    @Test
    @DisplayName("encodear retorna hash BCrypt válido")
    void encodear_retornaHash() {
        String hash = adapter.encodear("password123");

        assertThat(hash).isNotNull();
        assertThat(hash).isNotEmpty();
        assertThat(hash).startsWith("$2a$12$");
    }

    @Test
    @DisplayName("matches retorna true para password correcta")
    void matches_correcto_retornaTrue() {
        String hash = adapter.encodear("password123");

        assertThat(adapter.matches("password123", hash)).isTrue();
    }

    @Test
    @DisplayName("matches retorna false para password incorrecta")
    void matches_incorrecto_retornaFalse() {
        String hash = adapter.encodear("password123");

        assertThat(adapter.matches("wrongpassword", hash)).isFalse();
    }

    @Test
    @DisplayName("encodear genera hashes diferentes para misma password (salt aleatorio)")
    void encodear_mismaPassword_hashesDiferentes() {
        String hash1 = adapter.encodear("password123");
        String hash2 = adapter.encodear("password123");

        assertThat(hash1).isNotEqualTo(hash2);
    }
}
