package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;

public interface LoginUseCase {
    AuthResponseDto login(String rut, String password);
}
