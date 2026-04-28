package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.dto.RegistroRequestDto;
import cl.duoc.colegio.usuario.application.factory.UserStrategyFactory;
import cl.duoc.colegio.usuario.application.strategy.AuthorizationStrategy;
import cl.duoc.colegio.usuario.domain.exception.EmailYaRegistradoException;
import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.in.RegistroUseCase;
import cl.duoc.colegio.usuario.domain.port.out.PasswordEncoderPort;
import cl.duoc.colegio.usuario.domain.port.out.TokenGeneratorPort;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Registro de nuevo usuario.
 *
 * 1. Verifica que el email no esté registrado.
 * 2. Hashea la contraseña.
 * 3. Crea y persiste el usuario.
 * 4. Resuelve permisos via Strategy.
 * 5. Retorna JWT de sesión automática post-registro.
 */
@Service
public class RegistroUseCaseImpl implements RegistroUseCase {

    private final UsuarioRepositoryPort repositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenGeneratorPort tokenGeneratorPort;
    private final UserStrategyFactory strategyFactory;

    public RegistroUseCaseImpl(UsuarioRepositoryPort repositoryPort,
                               PasswordEncoderPort passwordEncoderPort,
                               TokenGeneratorPort tokenGeneratorPort,
                               UserStrategyFactory strategyFactory) {
        this.repositoryPort = repositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenGeneratorPort = tokenGeneratorPort;
        this.strategyFactory = strategyFactory;
    }

    @Override
    @Transactional
    public AuthResponseDto registrar(RegistroRequestDto request) {
        // 1. Verificar unicidad del email
        if (repositoryPort.existePorEmail(request.email())) {
            throw new EmailYaRegistradoException(request.email());
        }

        // 2. Hashear contraseña
        String passwordHash = passwordEncoderPort.encodear(request.password());

        // 3. Crear entidad de dominio
        Usuario nuevoUsuario = new Usuario(
                request.email(),
                passwordHash,
                request.rol(),
                request.nombre(),
                request.apellido()
        );

        // 4. Asociar perfil si viene en el request
        if (request.perfilId() != null) {
            nuevoUsuario.asociarPerfil(request.perfilId());
        }

        // 5. Persistir
        Usuario guardado = repositoryPort.guardar(nuevoUsuario);

        // 6. Strategy para permisos
        AuthorizationStrategy strategy = strategyFactory.crear(guardado.getRol());
        Permisos permisos = strategy.resolverPermisos(guardado);

        // 7. Generar token de sesión automática
        String token = tokenGeneratorPort.generarToken(guardado);

        return AuthResponseDto.of(
                token,
                guardado.getEmail(),
                guardado.getNombreCompleto(),
                guardado.getRol().name(),
                permisos.getRecursosPermitidos(),
                System.currentTimeMillis() + 86400000L
        );
    }
}
