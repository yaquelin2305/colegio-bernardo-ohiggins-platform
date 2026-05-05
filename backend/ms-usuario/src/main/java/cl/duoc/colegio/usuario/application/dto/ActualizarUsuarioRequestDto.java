package cl.duoc.colegio.usuario.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para actualizar datos de un usuario existente.
 * Solo permite modificar nombre, apellido y email.
 * RUT y rol son inmutables después de la creación.
 */
public record ActualizarUsuarioRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        String email
) {}
