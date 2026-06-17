package cl.duoc.colegio.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;

/**
 * Filtro Global de propagación de headers + self-access check — CAPA 2 de seguridad.
 *
 * Ejecuta DESPUÉS de que Spring Security (SecurityConfigProd) ya validó
 * el JWT y el RBAC. Corre como GlobalFilter con {@code @Order(HIGHEST_PRECEDENCE)}.
 *
 * <h3>Responsabilidades (en orden)</h3>
 * <ol>
 *   <li><b>Bypass en rutas públicas</b>: /actuator/**, /health, /info,
 *       /swagger-ui/**, /v3/api-docs/**, /api/v1/auth/login, /api/v1/auth/health</li>
 *   <li><b>Bypass total en perfil DEV</b>: si el perfil activo es "dev",
 *       no se aplica ninguna verificación ni propagación</li>
 *   <li><b>Self-access check para ESTUDIANTE</b>: si la ruta es
 *       {@code /api/bff/boletin/{uuid}} y el rol es ESTUDIANTE,
 *       verifica que el UUID de la ruta coincida con el userId del JWT.
 *       Si no coincide → 403 Forbidden</li>
 *   <li><b>Propagación de headers downstream</b>: inyecta en cada request
 *       saliente los headers:
 *       <ul>
 *         <li>{@code X-User-Id}   = RUT del usuario (subject del JWT)</li>
 *         <li>{@code X-User-Role} = rol sin prefijo ROLE_ (ej: "ADMIN")</li>
 *         <li>{@code X-User-Uuid} = userId del JWT (UUID interno)</li>
 *       </ul>
 *   </li>
 * </ol>
 *
 * <h3>¿Por qué existe si Spring Security ya validó?</h3>
 * Spring Security hace RBAC grueso (¿tiene el rol para esta ruta?).
 * Este filtro hace verificaciones FINAS que requieren lógica de negocio
 * específica (ej: ¿es ESTE estudiante el dueño de ESTE boletín?).
 * Además, propaga la identidad a los MS downstream sin que ellos tengan
 * que re-validar el token (modelo Trust the Gateway).
 *
 * <h3>Orden de ejecución</h3>
 * <pre>
 * 1. SecurityWebFilter (Spring Security) → JWT + RBAC
 * 2. Route Matching → decide a qué MS va la request
 * 3. ESTE filtro (@Order HIGHEST_PRECEDENCE) → self-access + headers
 * 4. NettyRoutingFilter → proxy HTTP al MS downstream
 * </pre>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtValidationFilter implements GlobalFilter {

    private static final String HEADER_USER_ID   = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String HEADER_USER_UUID = "X-User-Uuid";

    private final Environment environment;

    public JwtValidationFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // ── 1. Permitir login y health aunque SecurityContext esté vacío ──────
        //    (Spring Security ya los marcó permitAll, pero el contexto reactivo
        //     podría no tener autenticación aún)
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // ── 2. Perfil DEV: bypass total ───────────────────────────────────────
        if (isDevProfile()) {
            return chain.filter(exchange);
        }

        // ── 3. Leer autenticación del exchange (puesto por AuthenticationSuccessHandler)
        //      y propagar headers a los microservicios downstream ───────────────
        // ── 4. Check fino: ESTUDIANTE solo puede ver su propio boletín ────────
        org.springframework.security.core.Authentication auth =
                exchange.getAttribute("gateway.auth");

        if (auth != null) {
                    String rut = auth.getName();
                    String role = extractRole(auth);
                    Map<String, Object> details = getDetails(auth);
                    String userId = details != null
                            ? (String) details.getOrDefault("userId", "")
                            : "";

                    // ── Check fino: ESTUDIANTE self-access boletín ──────────
                    if (path.startsWith("/api/bff/boletin/")) {
                        String uuidEnRuta = extraerSegmento(path, "/api/bff/boletin/");
                        if ("ESTUDIANTE".equals(role)
                                && (userId.isEmpty() || !userId.equals(uuidEnRuta))) {
                            log.warn("[SELF] ESTUDIANTE intentó boletín de otro — userId: {}, ruta: {}",
                                    userId, uuidEnRuta);
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }
                    }

                    // ── Propagar headers downstream ──────────────────────────
                    ServerHttpRequest mutated = exchange.getRequest().mutate()
                            .header(HEADER_USER_ID, rut)
                            .header(HEADER_USER_ROLE, role)
                            .header(HEADER_USER_UUID, userId)
                            .build();

                    return chain.filter(exchange.mutate().request(mutated).build());
                }

        return chain.filter(exchange);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isPublicPath(String path) {
        return path.startsWith("/actuator/")
                || path.equals("/health") || path.equals("/info")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/health");
    }

    private boolean isDevProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }

    private String extractRole(org.springframework.security.core.Authentication auth) {
        return auth.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("")
                .replace("ROLE_", "");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getDetails(
            org.springframework.security.core.Authentication auth) {
        Object details = auth.getDetails();
        if (details instanceof Map) {
            return (Map<String, Object>) details;
        }
        return null;
    }

    private String extraerSegmento(String path, String prefix) {
        String after = path.substring(prefix.length());
        int slash = after.indexOf('/');
        return slash == -1 ? after : after.substring(0, slash);
    }
}
