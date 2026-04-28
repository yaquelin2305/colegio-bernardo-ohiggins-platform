package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.application.dto.RegistroRequestDto;
import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;

/**
 * Puerto de entrada (driving port) para el caso de uso de Registro.
 */
public interface RegistroUseCase {
    AuthResponseDto registrar(RegistroRequestDto request);
}
