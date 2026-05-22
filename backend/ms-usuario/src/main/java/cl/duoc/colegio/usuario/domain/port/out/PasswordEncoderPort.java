package cl.duoc.colegio.usuario.domain.port.out;

/**
 * Puerto de salida (driven port) — codificación de contraseñas.
 *
 * Abstrae el algoritmo de hash para que el dominio no dependa
 * de BCrypt ni de Spring Security. La implementación concreta
 * ({@code BcryptPasswordAdapter} en infraestructura) usa
 * {@code BCryptPasswordEncoder} con strength 12.
 *
 * <h3>¿Por qué un puerto para esto?</h3>
 * Porque es una decisión técnica (qué algoritmo de hash usar)
 * que puede cambiar en el futuro sin tocar el dominio.
 * Ej: migrar de BCrypt a Argon2 solo requeriría un nuevo adapter.
 */
public interface PasswordEncoderPort {
    String encodear(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
