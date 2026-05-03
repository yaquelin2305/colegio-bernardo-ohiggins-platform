package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.dto.RefreshRequestDto;
import cl.duoc.colegio.usuario.application.factory.UserStrategyFactory;
import cl.duoc.colegio.usuario.application.strategy.AuthorizationStrategy;
import cl.duoc.colegio.usuario.domain.exception.CredencialesInvalidasException;
import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.RefreshToken;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.in.RefreshTokenUseCase;
import cl.duoc.colegio.usuario.domain.port.out.RefreshTokenRepositoryPort;
import cl.duoc.colegio.usuario.domain.port.out.TokenGeneratorPort;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Renovación del access token via refresh token.
 *
 * Flujo:
 * 1. Buscar refresh token en BD — debe existir y estar vigente.
 * 2. Buscar usuario por ID del token.
 * 3. Revocar el refresh token actual (one-time use — rotación).
 * 4. Emitir nuevo access token + nuevo refresh token.
 *
 * Si el refresh token no existe, está expirado o fue revocado → 401.
 */
@Service
@Transactional
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private static final long ACCESS_TTL_MS = 86_400_000L;

    private final RefreshTokenRepositoryPort refreshRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final TokenGeneratorPort tokenGenerator;
    private final UserStrategyFactory strategyFactory;

    public RefreshTokenUseCaseImpl(RefreshTokenRepositoryPort refreshRepository,
                                    UsuarioRepositoryPort usuarioRepository,
                                    TokenGeneratorPort tokenGenerator,
                                    UserStrategyFactory strategyFactory) {
        this.refreshRepository = refreshRepository;
        this.usuarioRepository = usuarioRepository;
        this.tokenGenerator = tokenGenerator;
        this.strategyFactory = strategyFactory;
    }

    @Override
    public AuthResponseDto refresh(RefreshRequestDto request) {
        // 1. Buscar token vigente
        RefreshToken oldToken = refreshRepository
                .buscarVigente(request.refreshToken())
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "Refresh token inválido, expirado o ya utilizado"));

        // 2. Buscar usuario
        Usuario usuario = usuarioRepository
                .buscarPorId(oldToken.getUsuarioId())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado"));

        if (!usuario.puedeAutenticarse()) {
            throw new CredencialesInvalidasException("Cuenta inactiva");
        }

        // 3. Revocar token anterior (one-time use — previene replay attacks)
        oldToken.revocar();
        refreshRepository.guardar(oldToken);

        // 4. Generar nuevo par de tokens
        AuthorizationStrategy strategy = strategyFactory.crear(usuario.getRol());
        Permisos permisos = strategy.resolverPermisos(usuario);

        String newAccessToken  = tokenGenerator.generarToken(usuario);
        String newRefreshToken = tokenGenerator.generarRefreshToken(usuario);

        return AuthResponseDto.of(
                newAccessToken,
                newRefreshToken,
                usuario.getRut(),
                usuario.getNombreCompleto(),
                usuario.getRol().name(),
                permisos.getRecursosPermitidos(),
                System.currentTimeMillis() + ACCESS_TTL_MS
        );
    }
}
