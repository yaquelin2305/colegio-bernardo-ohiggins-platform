#set( $symbol_dollar = '$' )
package ${package}.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para producción.
 * Modelo trust-the-gateway: todas las rutas /api/** son permitidas.
 * El RBAC lo aplica el API Gateway via JwtValidationFilter.
 */
@Configuration
@EnableWebSecurity
@Profile("prod")
public class SecurityConfigProd {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/api/**").permitAll()  // trust-the-gateway
                        .anyRequest().denyAll()
                );

        return http.build();
    }
}
