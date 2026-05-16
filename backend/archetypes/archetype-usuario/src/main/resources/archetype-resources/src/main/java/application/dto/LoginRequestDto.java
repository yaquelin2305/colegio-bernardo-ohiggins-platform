#set( $symbol_dollar = '$' )
package ${package}.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para login — credencial por RUT chileno.
 */
public record LoginRequestDto(

        @NotBlank(message = "El RUT es obligatorio")
        @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]${symbol_dollar}", message = "Formato de RUT inválido (ej: 12345678-9)")
        String rut,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password
) {}
