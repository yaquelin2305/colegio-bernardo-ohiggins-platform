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
 * FILTRO GLOBAL DE PROPAGACIÓN DE HEADERS + CHECKS FINOS
 *
 * Spring Security ya validó JWT + RBAC (SecurityConfigProd/Docker).
 * Este filtro solo:
 *  1. Bypass en rutas públicas (whitelist auxiliar)
 *  2. Bypass total en perfil DEV
 *  3. Propagar X-User-Id / X-User-Role / X-User-Uuid desde SecurityContext
 *  4. Verificar ESTUDIANTE solo accede a su propio boletín
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
