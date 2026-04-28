package cl.duoc.colegio.usuario.application.dto;

import java.util.List;

/**
 * DTO de respuesta para Login y Registro exitosos.
 * Incluye el JWT y la información pública del usuario.
 */
public record AuthResponseDto(
        String token,
        String tipo,
        String email,
        String nombreCompleto,
        String rol,
        List<String> permisos,
        long expiraEn
) {
    public static AuthResponseDto of(String token, String email, String nombreCompleto,
                                      String rol, List<String> permisos, long expiraEn) {
        return new AuthResponseDto(token, "Bearer", email, nombreCompleto, rol, permisos, expiraEn);
    }
}
