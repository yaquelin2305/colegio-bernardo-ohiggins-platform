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
