package cl.duoc.colegio.usuario.infrastructure.adapter.in.rest;

import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.dto.LoginRequestDto;
import cl.duoc.colegio.usuario.application.dto.RegistroRequestDto;
import cl.duoc.colegio.usuario.domain.port.in.LoginUseCase;
import cl.duoc.colegio.usuario.domain.port.in.RegistroUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada: Controller REST para autenticación.
 *
 * Expone los endpoints del MS-Usuario al API Gateway.
 * Solo orquesta la llamada a los casos de uso — NADA de lógica de negocio aquí.
 *
 * Rutas:
 *  POST /api/v1/auth/login    → Autenticación
 *  POST /api/v1/auth/register → Registro
 *  GET  /api/v1/auth/health   → Health check
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegistroUseCase registroUseCase;

    public AuthController(LoginUseCase loginUseCase, RegistroUseCase registroUseCase) {
        this.loginUseCase = loginUseCase;
        this.registroUseCase = registroUseCase;
    }

    /**
     * Login de usuario existente.
     * Retorna JWT con claims de rol y permisos.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = loginUseCase.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Registro de nuevo usuario.
     * Retorna JWT de sesión automática post-registro.
     *
     * NOTA: En producción, el registro de usuarios debe ser realizado
     * exclusivamente por el ADMIN. Este endpoint está disponible para
     * facilitar el desarrollo y las pruebas de integración.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registrar(@Valid @RequestBody RegistroRequestDto request) {
        AuthResponseDto response = registroUseCase.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Health check del servicio.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("MS-Usuario operativo");
    }
}
