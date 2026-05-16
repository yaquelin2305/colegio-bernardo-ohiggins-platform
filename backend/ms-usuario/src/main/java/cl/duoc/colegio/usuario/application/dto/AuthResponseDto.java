package cl.duoc.colegio.usuario.application.dto;

import java.util.List;

/**
 * DTO de respuesta para Login y Registro exitosos.
 *
 * accessToken:  JWT de corta duración (24h). sub = RUT.
 */
public record AuthResponseDto(
        String accessToken,
        String tipo,
        String rut,
        String nombreCompleto,
        String rol,
        List<String> permisos,
        long expiraEn
) {
    public static AuthResponseDto of(String accessToken,
                                      String rut, String nombreCompleto,
                                      String rol, List<String> permisos, long expiraEn) {
        return new AuthResponseDto(
                accessToken, "Bearer",
                rut, nombreCompleto, rol, permisos, expiraEn
        );
    }
}
