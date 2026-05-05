package cl.duoc.colegio.usuario.application.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO de entrada para POST /auth/refresh */
public record RefreshRequestDto(
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {}
