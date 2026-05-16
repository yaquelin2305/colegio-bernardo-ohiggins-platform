#set( $symbol_dollar = '$' )
package ${package}.domain.port.in;

import ${package}.application.dto.LoginRequestDto;
import ${package}.application.dto.AuthResponseDto;

public interface LoginUseCase {

    AuthResponseDto login(LoginRequestDto request);
}
