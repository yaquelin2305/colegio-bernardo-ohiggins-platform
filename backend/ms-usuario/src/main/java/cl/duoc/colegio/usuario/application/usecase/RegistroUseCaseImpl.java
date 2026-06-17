package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.domain.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.factory.UserStrategyFactory;
import cl.duoc.colegio.usuario.application.strategy.AuthorizationStrategy;
import cl.duoc.colegio.usuario.domain.exception.EmailYaRegistradoException;
import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.in.RegistroUseCase;
import cl.duoc.colegio.usuario.domain.port.out.PasswordEncoderPort;
import cl.duoc.colegio.usuario.domain.port.out.TokenGeneratorPort;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementación del caso de uso Registro.
 *
 * Crea un nuevo usuario validando unicidad de RUT y email,
 * hashea la contraseña y retorna JWT con los permisos del rol.
 * Solo accesible por ADMIN via {@code POST /api/v1/admin/crear}.
 *
 * <h3>Reglas de negocio</h3>
 * <ul>
 *   <li>RUT y email deben ser únicos en el sistema</li>
 *   <li>Contraseña se hashea con BCrypt (strength 12) — nunca se almacena en texto plano</li>
 *   <li>El usuario se crea activo por defecto</li>
 *   <li>Si el rol es APODERADO, se asocia el UUID del pupilo ({@code pupiloUuid})</li>
 *   <li>Si se proporciona {@code perfilId}, se asocia al usuario (ej: id del docente)</li>
 * </ul>
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
    public AuthResponseDto registrar(String rut, String email, String password,
                                     String nombre, String apellido, RolUsuario rol,
                                     Long perfilId, UUID pupiloUuid) {

        if (repositoryPort.existePorRut(rut)) {
            throw new EmailYaRegistradoException("RUT ya registrado: " + rut);
        }

        if (repositoryPort.existePorEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        String passwordHash = passwordEncoderPort.encodear(password);

        Usuario nuevoUsuario = new Usuario(rut, email, passwordHash, rol, nombre, apellido);

        if (perfilId != null) {
            nuevoUsuario.asociarPerfil(perfilId);
        }
        if (pupiloUuid != null) {
            nuevoUsuario.asociarPupilo(pupiloUuid);
        }

        Usuario guardado = repositoryPort.guardar(nuevoUsuario);

        AuthorizationStrategy strategy = strategyFactory.crear(guardado.getRol());
        Permisos permisos = strategy.resolverPermisos(guardado);

        String token = tokenGeneratorPort.generarToken(guardado);

        return AuthResponseDto.of(
                token,
                guardado.getRut(),
                guardado.getNombreCompleto(),
                guardado.getRol().name(),
                permisos.getRecursosPermitidos(),
                System.currentTimeMillis() + 86400000L
        );
    }
}
