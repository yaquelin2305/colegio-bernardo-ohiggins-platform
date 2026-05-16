#set( $symbol_dollar = '$' )
package ${package}.application.usecase;

import ${package}.application.dto.AuthResponseDto;
import ${package}.application.dto.LoginRequestDto;
import ${package}.application.strategy.AuthorizationStrategy;
import ${package}.application.factory.AuthorizationStrategyFactory;
import ${package}.domain.exception.CredencialesInvalidasException;
import ${package}.domain.exception.UsuarioInactivoException;
import ${package}.domain.model.Usuario;
import ${package}.domain.port.in.LoginUseCase;
import ${package}.domain.port.out.PasswordEncoderPort;
import ${package}.domain.port.out.TokenPort;
import ${package}.domain.port.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginUseCaseImpl implements LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenPort tokenPort;
    private final AuthorizationStrategyFactory strategyFactory;

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto request) {
        // 1. Buscar usuario por RUT
        Usuario usuario = usuarioRepository.buscarPorRut(request.rut())
                .orElseThrow(CredencialesInvalidasException::new);

        // 2. Validar que esté activo
        if (!usuario.isActivo()) {
            throw new UsuarioInactivoException();
        }

        // 3. Verificar contraseña
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        // 4. Resolver permisos por rol (Strategy + Factory)
        AuthorizationStrategy strategy = strategyFactory.crear(usuario.getRol());
        List<String> recursos = strategy.getRecursos();

        // 5. Generar JWT
        String token = tokenPort.generarToken(usuario, recursos);

        return AuthResponseDto.builder()
                .accessToken(token)
                .tipo("Bearer")
                .rut(usuario.getRut())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol().name())
                .permisos(recursos)
                .expiraEn(System.currentTimeMillis() + 86_400_000L) // 24h
                .build();
    }
}
