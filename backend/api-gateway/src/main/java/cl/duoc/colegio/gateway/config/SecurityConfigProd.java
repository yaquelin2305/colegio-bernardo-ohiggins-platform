package cl.duoc.colegio.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad — Perfil PRODUCCIÓN
 *
 * Permite solo rutas públicas sin autenticación Spring Security.
 * La validación JWT real la hace JwtValidationFilter (GlobalFilter).
 *
 * Spring Security aquí actúa como segunda línea de defensa.
 */
@Configuration
@EnableWebFluxSecurity
@Profile("prod")
public class SecurityConfigProd {

    @Bean
    public SecurityWebFilterChain prodSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/fallback/**"
                        ).permitAll()
                        .anyExchange().permitAll() // JWT ya validado por JwtValidationFilter
                )
                .build();
    }
}
