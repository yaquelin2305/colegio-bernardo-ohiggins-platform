package cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto;

import cl.duoc.colegio.usuario.domain.model.Usuario;

import java.util.UUID;

public record UsuarioResponseDto(
        UUID id,
        String rut,
        String nombreCompleto,
        String email,
        String rol,
        boolean activo,
        UUID pupiloUuid
) {
    public static UsuarioResponseDto fromDomain(Usuario u) {
        return new UsuarioResponseDto(
                u.getId(),
                u.getRut(),
                u.getNombreCompleto(),
                u.getEmail(),
                u.getRol().name(),
                u.isActivo(),
                u.getPupiloUuid()
        );
    }
}
