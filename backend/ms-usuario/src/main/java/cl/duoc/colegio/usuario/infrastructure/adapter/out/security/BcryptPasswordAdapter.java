package cl.duoc.colegio.usuario.infrastructure.adapter.out.security;

import cl.duoc.colegio.usuario.domain.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida: Hashing de contraseñas con BCrypt.
 *
 * BCrypt es el estándar de la industria para passwords:
 * - Incluye salt automático (protección contra rainbow tables)
 * - Factor de costo configurable (actualmente 12 rondas)
 * - Algoritmo lento por diseño (dificulta ataques de fuerza bruta)
 */
@Component
public class BcryptPasswordAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public String encodear(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
