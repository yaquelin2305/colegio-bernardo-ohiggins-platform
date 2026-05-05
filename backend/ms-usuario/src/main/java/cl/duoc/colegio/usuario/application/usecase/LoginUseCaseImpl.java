package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.application.dto.LoginRequestDto;
import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.factory.UserStrategyFactory;
import cl.duoc.colegio.usuario.application.strategy.AuthorizationStrategy;
import cl.duoc.colegio.usuario.domain.exception.CredencialesInvalidasException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioInactivoException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioNoEncontradoException;
import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.in.LoginUseCase;
import cl.duoc.colegio.usuario.domain.port.out.PasswordEncoderPort;
import cl.duoc.colegio.usuario.domain.port.out.TokenGeneratorPort;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

/**
 * Caso de Uso: Login de usuario.
 *
 * 1. Busca el usuario por RUT.
 * 2. Verifica que esté activo.
 * 3. Verifica las credenciales.
 * 4. Usa el Factory para obtener la Strategy del rol.
 * 5. Resuelve los permisos via Strategy.
 * 6. Genera access token (sub=RUT) + refresh token (UUID opaco).
 *
 * NO conoce JPA, JWT concreto ni BCrypt — solo trabaja con puertos.
 */
@Service
public class LoginUseCaseImpl implements LoginUseCase {

    private static final long ACCESS_TTL_MS = 86_400_000L; // 24h

    private final UsuarioRepositoryPort repositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenGeneratorPort tokenGeneratorPort;
    private final UserStrategyFactory strategyFactory;

    public LoginUseCaseImpl(UsuarioRepositoryPort repositoryPort,
                            PasswordEncoderPort passwordEncoderPort,
                            TokenGeneratorPort tokenGeneratorPort,
                            UserStrategyFactory strategyFactory) {
        this.repositoryPort = repositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenGeneratorPort = tokenGeneratorPort;
        this.strategyFactory = strategyFactory;
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        // 1. Buscar por RUT
        Usuario usuario = repositoryPort
                .buscarPorRut(request.rut())
                .orElseThrow(() -> new UsuarioNoEncontradoException(request.rut()));

        // 2. Cuenta activa
        if (!usuario.puedeAutenticarse()) {
            throw new UsuarioInactivoException(request.rut());
        }

        // 3. Verificar contraseña
        if (!passwordEncoderPort.matches(request.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        // 4. Strategy por rol
        AuthorizationStrategy strategy = strategyFactory.crear(usuario.getRol());
        Permisos permisos = strategy.resolverPermisos(usuario);

        // 5. Generar access token (sub=RUT) + refresh token (opaco, 7 días)
        String accessToken  = tokenGeneratorPort.generarToken(usuario);
        String refreshToken = tokenGeneratorPort.generarRefreshToken(usuario);

        return AuthResponseDto.of(
                accessToken,
                refreshToken,
                usuario.getRut(),
                usuario.getNombreCompleto(),
                usuario.getRol().name(),
                permisos.getRecursosPermitidos(),
                System.currentTimeMillis() + ACCESS_TTL_MS
        );
    }
}
