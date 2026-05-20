package cl.duoc.colegio.usuario.infrastructure.adapter.in.rest;

import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.dto.NombreDto;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.in.GestionUsuariosUseCase;
import cl.duoc.colegio.usuario.domain.port.in.LoginUseCase;
import cl.duoc.colegio.usuario.domain.port.in.RegistroUseCase;
import cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto.ActualizarUsuarioRequestDto;
import cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto.LoginRequestDto;
import cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto.RegistroRequestDto;
import cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto.UsuarioResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegistroUseCase registroUseCase;
    private final GestionUsuariosUseCase gestionUseCase;

    public AuthController(LoginUseCase loginUseCase,
                          RegistroUseCase registroUseCase,
                          GestionUsuariosUseCase gestionUseCase) {
        this.loginUseCase = loginUseCase;
        this.registroUseCase = registroUseCase;
        this.gestionUseCase = gestionUseCase;
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(loginUseCase.login(request.rut(), request.password()));
    }

    @GetMapping("/api/v1/auth/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("MS-Usuario operativo");
    }

    @GetMapping("/api/v1/usuarios/{uuid}/nombre")
    public ResponseEntity<NombreDto> obtenerNombre(@PathVariable UUID uuid) {
        Usuario u = gestionUseCase.obtenerPorId(uuid);
        return ResponseEntity.ok(new NombreDto(u.getNombreCompleto()));
    }

    @PostMapping("/api/v1/admin/crear")
    public ResponseEntity<UsuarioResponseDto> crear(@Valid @RequestBody RegistroRequestDto request) {
        registroUseCase.registrar(
                request.rut(), request.email(), request.password(),
                request.nombre(), request.apellido(), request.rol(),
                request.perfilId(), request.pupiloUuid()
        );
        Usuario creado = gestionUseCase.obtenerPorRut(request.rut());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(creado));
    }

    @GetMapping("/api/v1/admin/{id}")
    public ResponseEntity<UsuarioResponseDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponseDto(gestionUseCase.obtenerPorId(id)));
    }

    @GetMapping("/api/v1/admin/listar/{rol}")
    public ResponseEntity<List<UsuarioResponseDto>> listarPorRol(@PathVariable String rol) {
        List<UsuarioResponseDto> usuarios = gestionUseCase.listarPorRol(rol).stream()
                .map(this::toResponseDto)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/api/v1/admin/actualizar/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarUsuarioRequestDto request) {
        return ResponseEntity.ok(
                toResponseDto(gestionUseCase.actualizar(
                        id, request.nombre(), request.apellido(), request.email()))
        );
    }

    @DeleteMapping("/api/v1/admin/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        gestionUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioResponseDto toResponseDto(Usuario u) {
        return UsuarioResponseDto.fromDomain(u);
    }
}
