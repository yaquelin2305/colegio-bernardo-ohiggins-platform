package cl.duoc.colegio.usuario.infrastructure.adapter.in.rest;

import cl.duoc.colegio.usuario.application.dto.ActualizarUsuarioRequestDto;
import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.dto.LoginRequestDto;
import cl.duoc.colegio.usuario.application.dto.RegistroRequestDto;
import cl.duoc.colegio.usuario.domain.exception.UsuarioNoEncontradoException;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.in.LoginUseCase;
import cl.duoc.colegio.usuario.domain.port.in.RegistroUseCase;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Adaptador de entrada: Controller REST del MS-Usuario.
 *
 * Rutas alineadas con el Contrato de API:
 *  PUT    /api/v1/admin/actualizar/{id}   → Actualizar datos de usuario (solo ADMIN)
 *  POST   /api/v1/auth/login            → Autenticación pública (rut + password)
 *  GET    /api/v1/auth/health           → Health check
 *
 *  POST   /api/v1/admin/crear           → Registro de usuario (solo ADMIN — validado en Gateway)
 *  GET    /api/v1/admin/listar/{rol}    → Listar usuarios por rol (solo ADMIN)
 *  DELETE /api/v1/admin/eliminar/{id}   → Soft Delete (solo ADMIN)
 */
@RestController
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegistroUseCase registroUseCase;
    private final UsuarioRepositoryPort repositoryPort;

    public AuthController(LoginUseCase loginUseCase,
                          RegistroUseCase registroUseCase,
                          UsuarioRepositoryPort repositoryPort) {
        this.loginUseCase = loginUseCase;
        this.registroUseCase = registroUseCase;
        this.repositoryPort = repositoryPort;
    }

    // ── Rutas públicas (/auth) ─────────────────────────────────────────────────

    /** Login: valida rut + password. Retorna accessToken. */
    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(loginUseCase.login(request));
    }

    /** Health check del servicio. */
    @GetMapping("/api/v1/auth/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("MS-Usuario operativo");
    }

    // ── Rutas de administración (/admin) — requieren JWT con rol ADMIN ────────

    /** Crear usuario (semántica administrativa, no auto-registro). */
    @PostMapping("/api/v1/admin/crear")
    public ResponseEntity<AuthResponseDto> crear(@Valid @RequestBody RegistroRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registroUseCase.registrar(request));
    }

    /**
     * Listar usuarios filtrando por Enum de rol.
     * GET /api/v1/admin/listar/DOCENTE, /listar/ESTUDIANTE, etc.
     */
    @GetMapping("/api/v1/admin/listar/{rol}")
    public ResponseEntity<List<Map<String, Object>>> listarPorRol(@PathVariable String rol) {
        RolUsuario rolEnum = RolUsuario.valueOf(rol.toUpperCase());
        List<Map<String, Object>> usuarios = repositoryPort.buscarPorRol(rolEnum.name())
                .stream()
                .map(u -> Map.<String, Object>of(
                        "id",            u.getId(),
                        "rut",           u.getRut(),
                        "nombreCompleto", u.getNombreCompleto(),
                        "email",         u.getEmail(),
                        "rol",           u.getRol().name(),
                        "activo",        u.isActivo()
                ))
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Actualizar usuario: nombre, apellido y email (RUT y rol son inmutables).
     * Solo ADMIN — validado por RBAC en Gateway.
     */
    @PutMapping("/api/v1/admin/actualizar/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarUsuarioRequestDto request) {

        Usuario usuario = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id.toString()));

        usuario.actualizar(request.nombre(), request.apellido(), request.email());
        Usuario actualizado = repositoryPort.guardar(usuario);

        return ResponseEntity.ok(Map.of(
                "id",            actualizado.getId(),
                "rut",           actualizado.getRut(),
                "nombreCompleto", actualizado.getNombreCompleto(),
                "email",         actualizado.getEmail(),
                "rol",           actualizado.getRol().name(),
                "activo",        actualizado.isActivo()
        ));
    }

    /**
     * Soft Delete: cambia activo = false, NO borra la fila.
     * Preserva integridad referencial (calificaciones, asistencias históricas).
     */
    @DeleteMapping("/api/v1/admin/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        Usuario usuario = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id.toString()));
        usuario.desactivar();
        repositoryPort.guardar(usuario);
        return ResponseEntity.noContent().build();
    }
}
