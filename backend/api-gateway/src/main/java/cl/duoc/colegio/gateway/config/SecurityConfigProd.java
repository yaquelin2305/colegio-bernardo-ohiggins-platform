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

/**
 * Configuración de seguridad — Perfil PROD (Docker Compose).
 *
 * CAPA 1 (gruesa): Spring Security valida JWT + RBAC por ruta.
 * CAPA 2 (fina): {@code JwtValidationFilter} agrega self-access check
 *                para estudiantes y propaga headers X-User-* downstream.
 *
 * <h3>Modelo Trust the Gateway</h3>
 * El JWT se valida AQUÍ (firma, expiración, rol). Los microservicios
 * downstream reciben headers {@code X-User-Id}, {@code X-User-Role},
 * {@code X-User-Uuid} y confían en que ya fueron autorizados.
 *
 * <h3>Tabla de RBAC</h3>
 * <pre>
 * /api/v1/admin/**              → ADMIN
 * /api/v1/asignacion-docente/** → ADMIN
 * /api/v1/cursos/**             → ADMIN, DOCENTE
 * /api/v1/calificaciones/**     → ADMIN, DOCENTE
 * /api/v1/reportes/**           → ADMIN, DOCENTE, APODERADO, ESTUDIANTE
 * /api/bff/boletin/**           → ADMIN, DOCENTE, APODERADO, ESTUDIANTE*
 * /api/bff/dashboard/**         → ADMIN
 * POST /api/v1/auth/login       → público (sin token)
 * GET  /api/v1/auth/health      → público
 * </pre>
 * * ESTUDIANTE tiene restricción adicional en JwtValidationFilter:
 *   solo puede ver su propio boletín (userId == uuid en ruta).
 *
 * <h3>Flujo del AuthenticationWebFilter</h3>
 * <ol>
 *   <li>{@link JwtServerAuthenticationConverter} extrae "Bearer token"</li>
 *   <li>{@link JwtReactiveAuthenticationManager} valida firma y claims</li>
 *   <li>AuthenticationSuccessHandler guarda auth en exchange.attributes</li>
 *   <li>NoOpServerSecurityContextRepository = sin sesiones (stateless)</li>
 *   <li>authorizeExchange aplica RBAC por patrón de ruta</li>
 * </ol>
 */
@Configuration
@EnableWebFluxSecurity
@Profile("prod")
public class SecurityConfigProd {

    @Bean
    public SecurityWebFilterChain prodSecurityFilterChain(
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

                        // ── BFF asistencia: escritura ADMIN+DOCENTE, lectura autenticado ──
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
