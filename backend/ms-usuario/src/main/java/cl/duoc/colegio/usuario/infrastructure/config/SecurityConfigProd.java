package cl.duoc.colegio.usuario.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad — Perfil PROD.
 *
 * RUTAS PÚBLICAS (sin token):
 *   POST /api/v1/auth/login    → autenticación inicial
 *   POST /api/v1/auth/refresh  → renovación de access token
 *   POST /api/v1/auth/logout   → revocación de refresh token
 *   GET  /api/v1/auth/health   → healthcheck
 *   /actuator/health, /actuator/info
 *
 * RUTAS PROTEGIDAS (con token):
 *   POST /api/v1/admin/crear        → ADMIN (validado en Gateway)
 *   GET  /api/v1/admin/listar/{rol} → ADMIN (validado en Gateway)
 *   DELETE /api/v1/admin/eliminar/{id} → ADMIN (validado en Gateway)
 *
 * NOTA: el control de roles por ruta vive en el Gateway (JwtValidationFilter).
 * Este MS confía en los headers X-User-Id / X-User-Role propagados por el Gateway.
 */
@Configuration
@EnableWebSecurity
@Profile("prod")
public class SecurityConfigProd {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh",
                    "/api/v1/auth/logout",
                    "/api/v1/auth/health",
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
