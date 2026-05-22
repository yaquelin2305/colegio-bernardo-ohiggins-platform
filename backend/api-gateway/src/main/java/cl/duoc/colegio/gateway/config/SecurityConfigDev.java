package cl.duoc.colegio.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad — Perfil DEV.
 *
 * DESACTIVA toda seguridad: cualquier request es permitida sin JWT.
 * Ideal para desarrollo local donde no se quiere generar tokens manualmente.
 *
 * <h3>¿Qué hace exactamente?</h3>
 * <ul>
 *   <li>CSRF desactivado (API REST, no usa sesiones)</li>
 *   <li>HTTP Basic y Form Login desactivados</li>
 *   <li>{@code anyExchange().permitAll()} — cero autenticación</li>
 * </ul>
 *
 * <h3>Precaución</h3>
 * Este perfil NUNCA debe usarse en producción o en Docker.
 * Para entornos productivos usar {@link SecurityConfigProd}.
 *
 * <h3>¿Por qué también se saltea el JwtValidationFilter en dev?</h3>
 * El {@code JwtValidationFilter} verifica {@code environment.getActiveProfiles()}
 * y hace bypass si detecta el perfil "dev". Así no hay doble configuración.
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
@Profile("dev")
public class SecurityConfigDev {
    @Bean
    public SecurityWebFilterChain devSecurityFilterChain(ServerHttpSecurity http) {
        log.warn("  PERFIL DEV ACTIVO — Seguridad JWT DESACTIVADA        ");

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
