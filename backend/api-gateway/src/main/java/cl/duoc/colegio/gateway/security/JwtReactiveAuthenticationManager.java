package cl.duoc.colegio.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager reactivo de autenticación JWT — CAPA 1 de seguridad del Gateway.
 *
 * Spring Security invoca este manager durante el filter chain para validar
 * el token JWT entrante. Es parte del modelo "Trust the Gateway": aquí se
 * verifica la firma, expiración y claims del token UNA sola vez, y los
 * microservicios downstream confían en los headers X-User-* propagados.
 *
 * <h3>Flujo</h3>
 * <ol>
 *   <li>Recibe el token (ya extraído por {@link JwtServerAuthenticationConverter})</li>
 *   <li>Valida firma HMAC-SHA256 contra {@code gateway.jwt.secret}</li>
 *   <li>Extrae claims: sub (RUT), role, userId, estudianteId, pupiloId</li>
 *   <li>Si no tiene claim 'role' → 401 (BadCredentials)</li>
 *   <li>Si expiró → 401 (CredentialsExpired)</li>
 *   <li>Construye {@link Authentication} con ROLE_* + details map</li>
 *   <li>El auth se almacena en {@code exchange.attributes["gateway.auth"]}
 *       para que el {@code JwtValidationFilter} lo lea después</li>
 * </ol>
 *
 * <h3>Respuesta HTTP</h3>
 * <ul>
 *   <li>Token válido → continúa el filter chain</li>
 *   <li>Token expirado → 401 con mensaje "Token expirado"</li>
 *   <li>Token malformado/sin rol → 401 con mensaje "Token inválido"</li>
 * </ul>
 */
@Slf4j
@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final String jwtSecret;

    public JwtReactiveAuthenticationManager(
            @Value("${gateway.jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(
                            jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String rut = claims.getSubject();
            String role = claims.get("role", String.class);
            String userId = claims.get("userId", String.class);

            if (role == null || role.isBlank()) {
                log.warn("[AUTH] Token sin claim 'role'");
                return Mono.error(new BadCredentialsException("Token sin rol definido"));
            }

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role)
            );

            Map<String, Object> details = new HashMap<>();
            details.put("rut", rut);
            details.put("userId", userId != null ? userId : "");
            Object estudianteId = claims.get("estudianteId");
            if (estudianteId != null) {
                details.put("estudianteId", estudianteId);
            }
            Object pupiloId = claims.get("pupiloId");
            if (pupiloId != null) {
                details.put("pupiloId", pupiloId);
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(rut, null, authorities);
            auth.setDetails(details);

            log.debug("[AUTH] JWT validado — rut: {}, role: {}", rut, role);
            return Mono.just(auth);

        } catch (ExpiredJwtException e) {
            log.warn("[AUTH] Token expirado");
            return Mono.error(new CredentialsExpiredException("Token expirado"));
        } catch (JwtException e) {
            log.warn("[AUTH] Token inválido — {}", e.getMessage());
            return Mono.error(new BadCredentialsException("Token inválido"));
        }
    }
}
