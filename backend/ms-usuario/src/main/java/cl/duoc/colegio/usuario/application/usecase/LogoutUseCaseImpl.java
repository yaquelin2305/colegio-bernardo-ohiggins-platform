package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.application.dto.RefreshRequestDto;
import cl.duoc.colegio.usuario.domain.exception.CredencialesInvalidasException;
import cl.duoc.colegio.usuario.domain.model.RefreshToken;
import cl.duoc.colegio.usuario.domain.port.in.LogoutUseCase;
import cl.duoc.colegio.usuario.domain.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Logout — revoca el refresh token del usuario.
 *
 * El access token sigue siendo válido hasta su expiración natural (24h).
 * Para invalidación inmediata del access token se requiere una blocklist
 * (Redis), que queda como mejora futura documentada aquí.
 */
@Service
@Transactional
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshRepository;

    public LogoutUseCaseImpl(RefreshTokenRepositoryPort refreshRepository) {
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void logout(RefreshRequestDto request) {
        RefreshToken token = refreshRepository
                .buscarVigente(request.refreshToken())
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "Refresh token no encontrado o ya revocado"));

        token.revocar();
        refreshRepository.guardar(token);
    }
}
