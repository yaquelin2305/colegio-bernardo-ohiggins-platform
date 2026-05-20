package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;

import java.util.UUID;

public interface RegistroUseCase {
    AuthResponseDto registrar(String rut, String email, String password,
                              String nombre, String apellido, RolUsuario rol,
                              Long perfilId, UUID pupiloUuid);
}
