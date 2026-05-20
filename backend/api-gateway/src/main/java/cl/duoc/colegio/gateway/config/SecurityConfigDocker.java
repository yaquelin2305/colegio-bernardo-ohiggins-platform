package cl.duoc.colegio.gateway.config;

import cl.duoc.colegio.gateway.security.JwtReactiveAuthenticationManager;
import cl.duoc.colegio.gateway.security.JwtServerAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@Profile("docker")
public class SecurityConfigDocker {

    @Bean
    public SecurityWebFilterChain dockerSecurityFilterChain(
            ServerHttpSecurity http,
            JwtReactiveAuthenticationManager authManager,
            JwtServerAuthenticationConverter authConverter) {

        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authManager);
        jwtFilter.setServerAuthenticationConverter(authConverter);
        jwtFilter.setSecurityContextRepository(
                NoOpServerSecurityContextRepository.getInstance());
        jwtFilter.setAuthenticationSuccessHandler((webFilterExchange, authentication) -> {
            webFilterExchange.getExchange().getAttributes()
                    .put("gateway.auth", authentication);
            return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
        });

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges
                        // ── Público ──────────────────────────────────────────
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/health", "/actuator/info", "/actuator/**").permitAll()
                        .pathMatchers("/health", "/info").permitAll()
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/auth/health").permitAll()

                        // ── ADMIN exclusivo ─────────────────────────────────
                        .pathMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/v1/asignacion-docente/**").hasRole("ADMIN")
                        .pathMatchers("/api/bff/dashboard/**").hasRole("ADMIN")

                        // ── ADMIN + DOCENTE ─────────────────────────────────
                        .pathMatchers("/api/v1/cursos/**").hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers("/api/v1/asignaturas/**").hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers("/api/v1/matriculas/**").hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers("/api/v1/calificaciones/**").hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers("/api/v1/notas/**").hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers("/api/v1/estudiantes/**").hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers("/api/v1/asistencias/**").hasAnyRole("ADMIN", "DOCENTE")

                        // ── ADMIN + DOCENTE + APODERADO + ESTUDIANTE ────────
                        .pathMatchers("/api/v1/reportes/**")
                            .hasAnyRole("ADMIN", "DOCENTE", "APODERADO", "ESTUDIANTE")
                        .pathMatchers(HttpMethod.GET, "/api/bff/boletin/**")
                            .hasAnyRole("ADMIN", "DOCENTE", "APODERADO", "ESTUDIANTE")

                        // ── BFF asistencia ──────────────────────────────────
                        .pathMatchers(HttpMethod.POST, "/api/bff/asistencia/registrar")
                            .hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers(HttpMethod.POST, "/api/bff/asistencia/anotaciones")
                            .hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers(HttpMethod.PUT, "/api/bff/asistencia/**")
                            .hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers(HttpMethod.PATCH, "/api/bff/asistencia/**")
                            .hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers(HttpMethod.DELETE, "/api/bff/asistencia/**")
                            .hasAnyRole("ADMIN", "DOCENTE")
                        .pathMatchers("/api/bff/asistencia/**").authenticated()

                        // ── BFF comunicaciones ──────────────────────────────
                        .pathMatchers("/api/bff/comunicaciones/**").authenticated()

                        // ── BFF status ──────────────────────────────────────
                        .pathMatchers("/api/bff/status").authenticated()

                        // ── MS-Comunicaciones ───────────────────────────────
                        .pathMatchers("/api/v1/comunicaciones/**").authenticated()

                        // ── MS-Asistencia ───────────────────────────────────
                        .pathMatchers("/api/asistencia/**").authenticated()

                        // ── Usuarios lookup ─────────────────────────────────
                        .pathMatchers("/api/v1/usuarios/**").authenticated()

                        // ── Resto: autenticado ──────────────────────────────
                        .anyExchange().authenticated()
                )
                .build();
    }
}
