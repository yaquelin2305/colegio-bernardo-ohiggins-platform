package cl.duoc.colegio.usuario.application.dto;

import java.util.List;

/**
 * DTO de respuesta para Login y Registro exitosos.
 *
 * accessToken:  JWT de corta duración (24h). sub = RUT.
 * refreshToken: Token opaco (UUID v4, 7 días). Persiste en BD.
 *               Usar en POST /auth/refresh para renovar el accessToken.
 */
public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String tipo,
        String rut,
        String nombreCompleto,
        String rol,
        List<String> permisos,
        long expiraEn
) {
    public static AuthResponseDto of(String accessToken, String refreshToken,
                                      String rut, String nombreCompleto,
                                      String rol, List<String> permisos, long expiraEn) {
        return new AuthResponseDto(
                accessToken, refreshToken, "Bearer",
                rut, nombreCompleto, rol, permisos, expiraEn
        );
    }
}
