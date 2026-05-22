package cl.duoc.colegio.gateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Convertidor que extrae el token JWT del header Authorization.
 *
 * Busca el header {@code Authorization: Bearer <token>} en cada request.
 * Si no existe o no tiene el prefijo "Bearer ", retorna Mono.empty()
 * (usuario no autenticado → Spring Security decide según las reglas
 * de authorizeExchange si la ruta requiere auth o es pública).
 *
 * <h3>Formato esperado</h3>
 * <pre>
 * GET /api/v1/cursos HTTP/1.1
 * Host: localhost:8080
 * Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
 * </pre>
 *
 * <h3>¿Por qué token como principal y credencial?</h3>
 * Spring Security espera un {@code Authentication} object. Como no tenemos
 * username/password tradicional, usamos el token string como ambos.
 * El {@link JwtReactiveAuthenticationManager} lo valida realmente.
 */
@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return Mono.empty();
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
    }
}
