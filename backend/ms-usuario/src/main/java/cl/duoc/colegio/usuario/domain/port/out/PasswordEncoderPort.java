package cl.duoc.colegio.usuario.domain.port.out;

/**
 * Puerto de salida para el hash y verificación de contraseñas.
 */
public interface PasswordEncoderPort {
    String encodear(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
