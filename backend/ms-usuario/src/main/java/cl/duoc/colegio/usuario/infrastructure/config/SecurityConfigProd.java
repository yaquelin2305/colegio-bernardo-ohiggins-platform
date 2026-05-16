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
 * Modelo trust-the-gateway: todas las rutas /api/** son permitidas.
 * El API Gateway ya validó el JWT y aplicó RBAC.
 *
 * RUTAS PÚBLICAS (sin token):
 *   POST /api/v1/auth/login    → autenticación inicial
 *   GET  /api/v1/auth/health   → healthcheck
 *   /actuator/health, /actuator/info
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
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
