package cl.duoc.colegio.usuario.application.usecase;

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
