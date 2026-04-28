package cl.duoc.colegio.usuario.application.dto;

import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para el caso de uso de Registro.
 */
public record RegistroRequestDto(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotNull(message = "El rol es obligatorio")
        RolUsuario rol,

        /** Opcional: ID del perfil en MS-Académico (estudiante, docente, etc.) */
        Long perfilId
) {}
