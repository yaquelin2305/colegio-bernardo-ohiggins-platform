package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.application.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.application.dto.RefreshRequestDto;

/** Puerto de entrada: renovación de access token via refresh token */
public interface RefreshTokenUseCase {
    AuthResponseDto refresh(RefreshRequestDto request);
}
