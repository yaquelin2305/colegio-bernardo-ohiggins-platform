package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.domain.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.domain.exception.CredencialesInvalidasException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioInactivoException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioNoEncontradoException;

/**
 * Puerto de entrada (driving port) — caso de uso Login.
 *
 * Define el contrato que la capa de Infraestructura (REST) usa para autenticar.
 * Pertenece a la capa de Dominio: no depende de Spring, JPA ni HTTP.
 *
 * <h3>Flujo</h3>
 * <ol>
 *   <li>Busca usuario por RUT → si no existe, lanza {@link UsuarioNoEncontradoException}</li>
 *   <li>Verifica que esté activo → si no, lanza {@link UsuarioInactivoException}</li>
 *   <li>Valida contraseña con BCrypt → si no coincide, lanza {@link CredencialesInvalidasException}</li>
 *   <li>Obtiene permisos según rol (Strategy → Factory Method)</li>
 *   <li>Genera JWT con claims del usuario</li>
 * </ol>
 *
 * @implNote Implementado por {@code LoginUseCaseImpl} en capa Application.
 *           Los puertos de salida (repository, password encoder, token generator)
 *           son interfaces del dominio inyectadas por Spring.
 */
public interface LoginUseCase {
    AuthResponseDto login(String rut, String password);
}
