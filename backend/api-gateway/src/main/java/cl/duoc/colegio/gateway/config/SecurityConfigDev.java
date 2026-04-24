package cl.duoc.colegio.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad — Perfil DESARROLLO
 *
 * Permite TODAS las rutas sin autenticación.
 * Útil para pruebas locales sin necesitar un JWT válido.
 *
 * ¡NUNCA usar en producción!
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
@Profile("dev")
public class SecurityConfigDev {

    @Bean
    public SecurityWebFilterChain devSecurityFilterChain(ServerHttpSecurity http) {
        log.warn("=======================================================");
        log.warn("  PERFIL DEV ACTIVO — Seguridad JWT DESACTIVADA        ");
        log.warn("  NO usar en entornos productivos                       ");
        log.warn("=======================================================");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .build();
    }
}
