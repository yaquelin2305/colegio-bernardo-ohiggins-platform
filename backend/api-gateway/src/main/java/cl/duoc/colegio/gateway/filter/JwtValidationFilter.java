package cl.duoc.colegio.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * FILTRO GLOBAL DE VALIDACIÓN JWT + RBAC
 *
 * Responsabilidades (en orden de ejecución):
 *  1. Bypass en rutas públicas (whitelist)
 *  2. Bypass total en perfil DEV
 *  3. Validar presencia y firma del Bearer token
 *  4. Extraer sub (RUT) y claim "role"
 *  5. Aplicar RBAC por ruta
 *  6. Propagar X-User-Id (RUT) y X-User-Role a los microservicios downstream
 *
 * Rutas públicas (sin token):
 *  POST /api/v1/auth/login
 *  POST /api/v1/auth/refresh
 *  GET  /api/v1/auth/health
 *  /actuator/health, /actuator/info, /fallback/**, /swagger-ui/**, /v3/api-docs/**
 *
 * RBAC:
 *  /api/v1/admin/**              → solo ADMIN
 *  /api/v1/cursos/**             → ADMIN, DOCENTE
 *  /api/v1/asignaturas/**        → ADMIN, DOCENTE
 *  /api/v1/asignacion-docente/** → ADMIN
 *  /api/v1/matriculas/**         → ADMIN, DOCENTE
 *  /api/bff/boletin/**           → ADMIN, DOCENTE, APODERADO, ESTUDIANTE*
 *  /api/bff/dashboard/**         → ADMIN
 *
 *  (*) ESTUDIANTE solo puede acceder a /boletin/{su-propio-UUID}
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtValidationFilter implements GlobalFilter {

    private static final String BEARER_PREFIX  = "Bearer ";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String HEADER_USER_UUID = "X-User-Uuid";

    /**
     * Rutas completamente públicas — no necesitan token bajo ningún perfil.
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/api/v1/auth/health",
            "/actuator/health",
            "/actuator/info",
            "/fallback/",
            "/swagger-ui",
            "/v3/api-docs",
            "/health",
            "/info"
    );

    /**
     * Rutas que requieren rol ADMIN exclusivamente.
     */
    private static final List<String> ADMIN_ONLY_PATHS = List.of(
            "/api/v1/admin/",
            "/api/v1/asignacion-docente",
            "/api/bff/dashboard/"
    );

    /**
     * Rutas que requieren ADMIN o DOCENTE.
     */
    private static final List<String> ADMIN_DOCENTE_PATHS = List.of(
            "/api/v1/cursos/",
            "/api/v1/asignaturas/",
            "/api/v1/matriculas/"
    );

    @Value("${gateway.jwt.secret}")
    private String jwtSecret;

    private final Environment environment;

    public JwtValidationFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path   = exchange.getRequest().getPath().toString();
        HttpMethod method = exchange.getRequest().getMethod();

        // ── 1. Rutas públicas: bypass sin importar perfil ─────────────────────
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // ── 2. Perfil DEV: bypass total ───────────────────────────────────────
        if (isDevProfile()) {
            log.debug("[DEV] JWT desactivado — path: {}", path);
            return chain.filter(exchange);
        }

        // ── 3. Extraer y validar token ────────────────────────────────────────
        String authHeader = exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("[AUTH] Token ausente — method: {} path: {}", method, path);
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Token de autenticación requerido");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        Claims claims;
        try {
            claims = validateToken(token);
        } catch (ExpiredJwtException e) {
            log.warn("[AUTH] Token expirado — path: {}", path);
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Token expirado");
        } catch (SignatureException | MalformedJwtException e) {
            log.warn("[AUTH] Token inválido — path: {}", path);
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Token inválido");
        } catch (Exception e) {
            log.error("[AUTH] Error inesperado validando token — path: {}", path, e);
            return writeError(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno de autenticación");
        }

        // sub = RUT (tras el fix del emisor)
        String rut    = claims.getSubject();
        String role   = claims.get("role", String.class);
        String userId = claims.get("userId", String.class); // UUID interno

        if (role == null || role.isBlank()) {
            log.warn("[AUTH] Token sin claim 'role' — path: {}", path);
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Token sin rol definido");
        }

        // ── 4. RBAC por ruta ──────────────────────────────────────────────────
        Mono<Void> rbacError = checkRbac(exchange, path, role, userId);
        if (rbacError != null) return rbacError;

        // ── 5. Propagación de headers a los microservicios downstream ─────────
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header(HEADER_USER_ID,   rut)     // Identidad de negocio (RUT)
                .header(HEADER_USER_ROLE, role)
                .header(HEADER_USER_UUID, userId != null ? userId : "")
                .build();

        log.debug("[AUTH] OK — rut: {}, role: {}, path: {}", rut, role, path);
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    // ── RBAC ──────────────────────────────────────────────────────────────────

    /**
     * Verifica que el rol del token tenga permisos para la ruta.
     * Retorna un Mono de error si falla, null si pasa.
     */
    private Mono<Void> checkRbac(ServerWebExchange exchange, String path,
                                   String role, String userId) {

        // /api/v1/admin/** → solo ADMIN
        if (matchesAny(path, ADMIN_ONLY_PATHS)) {
            if (!"ADMIN".equals(role)) {
                log.warn("[RBAC] Acceso denegado a ruta admin — role: {}, path: {}", role, path);
                return writeError(exchange, HttpStatus.FORBIDDEN,
                        "Acceso restringido a administradores");
            }
        }

        // /api/v1/cursos/**, /asignaturas/**, /matriculas/** → ADMIN o DOCENTE
        if (matchesAny(path, ADMIN_DOCENTE_PATHS)) {
            if (!"ADMIN".equals(role) && !"DOCENTE".equals(role)) {
                log.warn("[RBAC] Acceso denegado a ruta académica — role: {}, path: {}", role, path);
                return writeError(exchange, HttpStatus.FORBIDDEN,
                        "Acceso restringido a administradores y docentes");
            }
        }

        // /api/bff/boletin/{estudianteId} — ESTUDIANTE solo puede ver su propio boletín
        if (path.startsWith("/api/bff/boletin/")) {
            String estudianteIdEnRuta = extraerSegmento(path, "/api/bff/boletin/");

            if ("ESTUDIANTE".equals(role)) {
                // El userId en el token es el UUID interno del estudiante
                if (userId == null || !userId.equals(estudianteIdEnRuta)) {
                    log.warn("[RBAC] ESTUDIANTE intentó acceder al boletín de otro — token.userId: {}, ruta: {}",
                            userId, estudianteIdEnRuta);
                    return writeError(exchange, HttpStatus.FORBIDDEN,
                            "Solo puedes consultar tu propio boletín");
                }
            } else if ("APODERADO".equals(role)) {
                // El apoderado puede ver el boletín del pupilo asociado en su token
                // El claim "pupiloId" fue generado por ApoderadoAuthorizationStrategy
                // La validación de que pupiloId == estudianteIdEnRuta queda para el BFF
                // ya que el Gateway no tiene acceso a la BD para verificar la relación
            } else if (!"ADMIN".equals(role) && !"DOCENTE".equals(role)) {
                return writeError(exchange, HttpStatus.FORBIDDEN, "Acceso no autorizado al boletín");
            }
        }

        return null; // PASS
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(
                        jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isDevProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }

    private boolean matchesAny(String path, List<String> patterns) {
        return patterns.stream().anyMatch(path::startsWith);
    }

    private String extraerSegmento(String path, String prefix) {
        String after = path.substring(prefix.length());
        int slash = after.indexOf('/');
        return slash == -1 ? after : after.substring(0, slash);
    }

    /**
     * Respuesta de error RFC 7807 Problem Details — sin stacktrace.
     */
    private Mono<Void> writeError(ServerWebExchange exchange,
                                   HttpStatus status, String detail) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        String body = String.format("""
                {
                  "type": "about:blank",
                  "title": "%s",
                  "status": %d,
                  "detail": "%s",
                  "timestamp": "%s"
                }""",
                status.getReasonPhrase(),
                status.value(),
                detail,
                Instant.now().toString()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
