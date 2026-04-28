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
 * Orquesta la autenticación completa:
 * 1. Busca el usuario por email.
 * 2. Verifica que esté activo.
 * 3. Verifica las credenciales.
 * 4. Usa el Factory para obtener la Strategy del rol.
 * 5. Resuelve los permisos via Strategy.
 * 6. Genera el JWT con claims personalizados.
 *
 * Este servicio NO conoce JPA, JWT concreto, ni BCrypt.
 * Trabaja SOLO con puertos (interfaces). Eso es Arquitectura Hexagonal.
 */
@Service
public class LoginUseCaseImpl implements LoginUseCase {

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
        // 1. Buscar usuario — lanza excepción si no existe
        Usuario usuario = repositoryPort
                .buscarPorEmail(request.email())
                .orElseThrow(() -> new UsuarioNoEncontradoException(request.email()));

        // 2. Verificar que la cuenta esté activa
        if (!usuario.puedeAutenticarse()) {
            throw new UsuarioInactivoException(request.email());
        }

        // 3. Verificar contraseña
        if (!passwordEncoderPort.matches(request.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        // 4. Obtener Strategy según rol (Factory Method)
        AuthorizationStrategy strategy = strategyFactory.crear(usuario.getRol());

        // 5. Resolver permisos via Strategy
        Permisos permisos = strategy.resolverPermisos(usuario);

        // 6. Generar JWT con claims personalizados
        String token = tokenGeneratorPort.generarToken(usuario);

        return AuthResponseDto.of(
                token,
                usuario.getEmail(),
                usuario.getNombreCompleto(),
                usuario.getRol().name(),
                permisos.getRecursosPermitidos(),
                System.currentTimeMillis() + 86400000L // 24 horas
        );
    }
}
