package cl.duoc.colegio.usuario.infrastructure.adapter.out.security;

import cl.duoc.colegio.usuario.application.factory.UserStrategyFactory;
import cl.duoc.colegio.usuario.application.strategy.AuthorizationStrategy;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.out.TokenGeneratorPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Adaptador de salida: Generación y validación de JWT.
 *
 * ACCESS TOKEN (24h):
 *   sub  = RUT del usuario (identificador de negocio, no técnico)
 *   rol  = ADMIN | DOCENTE | ESTUDIANTE | APODERADO
 *   userId = UUID interno (para correlación interna entre MSs)
 *   nombre = nombre completo
 *   + claims específicos del rol via Strategy
 */
@Component
public class JwtTokenAdapter implements TokenGeneratorPort {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenAdapter.class);

    private static final long ACCESS_TOKEN_TTL_MS = 86_400_000L;        // 24 horas

    private final Key secretKey;
    private final UserStrategyFactory strategyFactory;

    public JwtTokenAdapter(
            @Value("${jwt.secret:colegio-bernardo-ohiggins-secret-key-2024-duoc-fs3-very-long-secret}")
            String secret,
            UserStrategyFactory strategyFactory) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.strategyFactory = strategyFactory;
    }

    // ── Access Token ──────────────────────────────────────────────────────────

    @Override
    public String generarToken(Usuario usuario) {
        AuthorizationStrategy strategy = strategyFactory.crear(usuario.getRol());
        Map<String, Object> claimsAdicionales = strategy.generarClaimsAdicionales(usuario);

        long ahora = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                // FIX CRÍTICO: sub = RUT (identificador de negocio, no email)
                .setSubject(usuario.getRut())
                .setIssuedAt(new Date(ahora))
                .setExpiration(new Date(ahora + ACCESS_TOKEN_TTL_MS))
                .setIssuer("ms-usuario")
                // userId en claim separado para correlación interna entre MSs
                .claim("userId", usuario.getId().toString())
                .claim("email", usuario.getEmail())
                .claim("nombre", usuario.getNombreCompleto())
                // Claim "role" (singular) — consistente con lo que lee el Gateway
                .claim("role", usuario.getRol().name());

        claimsAdicionales.forEach(builder::claim);

        return builder
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Validación ─────────────────────────────────────────────────────────────

    @Override
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Token no soportado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Token malformado: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Token inválido: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public String extraerRut(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // sub = RUT
    }
}
