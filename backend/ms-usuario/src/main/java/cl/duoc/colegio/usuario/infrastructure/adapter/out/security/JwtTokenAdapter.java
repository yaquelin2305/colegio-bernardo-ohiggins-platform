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
 * Genera tokens con claims personalizados según el rol del usuario.
 * El API Gateway usa estos claims para autorizar sin consultar este MS.
 *
 * Claims incluidos:
 * - sub: email del usuario
 * - rol: DOCENTE | APODERADO | ESTUDIANTE | ADMIN
 * - nombre: nombre completo
 * - permisos: recursos accesibles
 * - Claims específicos del rol (pupiloId, estudianteId, perfilId, etc.)
 */
@Component
public class JwtTokenAdapter implements TokenGeneratorPort {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenAdapter.class);

    private static final long EXPIRACION_MS = 86_400_000L; // 24 horas

    private final Key secretKey;
    private final UserStrategyFactory strategyFactory;

    public JwtTokenAdapter(
            @Value("${jwt.secret:colegio-bernardo-ohiggins-secret-key-2024-duoc-fs3-very-long-secret}")
            String secret,
            UserStrategyFactory strategyFactory) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.strategyFactory = strategyFactory;
    }

    @Override
    public String generarToken(Usuario usuario) {
        // Obtener claims adicionales según el rol via Strategy
        AuthorizationStrategy strategy = strategyFactory.crear(usuario.getRol());
        Map<String, Object> claimsAdicionales = strategy.generarClaimsAdicionales(usuario);

        long ahora = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                .setSubject(usuario.getEmail())
                .setIssuedAt(new Date(ahora))
                .setExpiration(new Date(ahora + EXPIRACION_MS))
                .setIssuer("ms-usuario")
                .claim("userId", usuario.getId().toString())
                .claim("nombre", usuario.getNombreCompleto())
                .claim("rol", usuario.getRol().name());

        // Añadir claims específicos del rol (pupiloId, estudianteId, etc.)
        claimsAdicionales.forEach(builder::claim);

        return builder
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

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
    public String extraerEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
