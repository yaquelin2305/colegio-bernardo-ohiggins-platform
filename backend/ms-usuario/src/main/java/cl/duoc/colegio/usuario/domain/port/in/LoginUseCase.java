package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.application.dto.LoginRequestDto;
import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;

/**
 * Puerto de entrada (driving port) para el caso de uso de Login.
 *
 * Define el contrato que el adaptador REST debe usar para autenticar.
 * La implementación vive en la capa de Aplicación.
 */
public interface LoginUseCase {
    AuthResponseDto login(LoginRequestDto request);
}
