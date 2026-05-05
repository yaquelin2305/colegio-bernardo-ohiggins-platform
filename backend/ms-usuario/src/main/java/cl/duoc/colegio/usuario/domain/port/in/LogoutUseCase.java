package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.application.dto.RefreshRequestDto;

/** Puerto de entrada: revocación del refresh token (logout) */
public interface LogoutUseCase {
    void logout(RefreshRequestDto request);
}
