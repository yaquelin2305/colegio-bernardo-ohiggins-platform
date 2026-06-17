#set( $symbol_dollar = '$' )
package ${package}.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthResponseDto {

    private String accessToken;
    private String tipo;
    private String rut;
    private String nombreCompleto;
    private String rol;
    private List<String> permisos;
    private long expiraEn;
}
