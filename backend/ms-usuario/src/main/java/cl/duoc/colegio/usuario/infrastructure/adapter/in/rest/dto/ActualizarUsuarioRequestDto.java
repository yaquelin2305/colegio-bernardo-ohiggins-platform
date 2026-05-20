package cl.duoc.colegio.usuario.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ActualizarUsuarioRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        String email
) {}
