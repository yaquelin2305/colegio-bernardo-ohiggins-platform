package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.domain.dto.AuthResponseDto;
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
 * Implementación del caso de uso Login.
 *
 * Capa: APPLICATION (orquesta la lógica de negocio).
 * Depende de 3 puertos de salida (interfaces del dominio) + 1 fábrica.
 * NO conoce los adaptadores concretos — Spring inyecta las implementaciones.
 *
 * <h3>Flujo paso a paso</h3>
 * <pre>
 * POST /api/v1/auth/login
 *   │
 *   ├─ 1. repositoryPort.buscarPorRut(rut)    → Usuario o excepción
 *   ├─ 2. usuario.puedeAutenticarse()          → lógica de dominio (activo?)
 *   ├─ 3. passwordEncoderPort.matches(...)     → BCrypt verify
 *   ├─ 4. strategyFactory.crear(rol)           → Factory → Strategy
 *   ├─ 5. strategy.resolverPermisos(usuario)   → Permisos (VO)
 *   ├─ 6. tokenGeneratorPort.generarToken(...) → JWT con claims
 *   └─ 7. AuthResponseDto (token + permisos)
 * </pre>
 *
 * <h3>Patrones aplicados</h3>
 * <ul>
 *   <li>Hexagonal: depende solo de interfaces (ports), no de adaptadores</li>
 *   <li>Strategy + Factory: permisos variables por rol sin if/else</li>
 *   <li>Constructor injection: sin {@code @Autowired}</li>
 * </ul>
 */
@Service
public class LoginUseCaseImpl implements LoginUseCase {

    private static final long ACCESS_TTL_MS = 86_400_000L;

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
    public AuthResponseDto login(String rut, String password) {
        Usuario usuario = repositoryPort
                .buscarPorRut(rut)
                .orElseThrow(() -> new UsuarioNoEncontradoException(rut));

        if (!usuario.puedeAutenticarse()) {
            throw new UsuarioInactivoException(rut);
        }

        if (!passwordEncoderPort.matches(password, usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        AuthorizationStrategy strategy = strategyFactory.crear(usuario.getRol());
        Permisos permisos = strategy.resolverPermisos(usuario);

        String accessToken = tokenGeneratorPort.generarToken(usuario);

        return AuthResponseDto.of(
                accessToken,
                usuario.getRut(),
                usuario.getNombreCompleto(),
                usuario.getRol().name(),
                permisos.getRecursosPermitidos(),
                System.currentTimeMillis() + ACCESS_TTL_MS
        );
    }
}
