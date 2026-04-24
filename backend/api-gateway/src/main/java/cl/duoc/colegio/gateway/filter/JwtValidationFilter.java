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

/**
 * FILTRO GLOBAL DE VALIDACIÓN JWT
 *
 * Intercepta TODAS las peticiones entrantes al Gateway.
 *
 * Comportamiento por perfil:
 *  - Perfil "dev"  → bypass completo (sin validar token)
 *  - Perfil "prod" → validación obligatoria del Bearer token
 *
 * Rutas públicas (siempre permitidas sin token):
 *  - /actuator/health
 *  - /fallback/**
 *  - /swagger-ui/**
 *  - /v3/api-docs/**
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtValidationFilter implements GlobalFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final List<String> PUBLIC_PATHS = List.of(
            "/actuator/health",
            "/actuator/info",
            "/fallback/",
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Value("${gateway.jwt.secret}")
    private String jwtSecret;

    private final Environment environment;

    public JwtValidationFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // --- Rutas públicas: siempre pasar ---
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // --- Perfil DEV: bypass total de seguridad ---
        if (isDevProfile()) {
            log.debug("[DEV] Seguridad JWT desactivada — path: {}", path);
            return chain.filter(exchange);
        }

        // --- Perfil PROD: validación obligatoria ---
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("[AUTH] Token ausente o formato inválido — path: {}", path);
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                    "Token de autenticación requerido");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            Claims claims = validateToken(token);

            // Propagamos el userId y rol al microservicio downstream como headers
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();

            log.debug("[AUTH] Token válido — user: {}, role: {}",
                    claims.getSubject(), claims.get("role"));

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException e) {
            log.warn("[AUTH] Token expirado — path: {}", path);
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token expirado");

        } catch (SignatureException | MalformedJwtException e) {
            log.warn("[AUTH] Token inválido — path: {}", path);
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token inválido");

        } catch (Exception e) {
            log.error("[AUTH] Error inesperado validando token", e);
            return writeErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error interno al validar la autenticación");
        }
    }

    // ===== MÉTODOS PRIVADOS =====

    private Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
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

    /**
     * Escribe una respuesta de error en formato RFC 7807 Problem Details.
     * Consistente con el manejo de errores del MS-Académico.
     */
    private Mono<Void> writeErrorResponse(ServerWebExchange exchange,
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
