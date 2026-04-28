package cl.duoc.colegio.usuario.domain.port.out;

import cl.duoc.colegio.usuario.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida (driven port) para la persistencia de usuarios.
 *
 * Define el contrato que la capa de Dominio/Aplicación necesita de la BD.
 * La implementación concreta (JPA) vive en Infraestructura.
 */
public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(UUID id);
    Usuario guardar(Usuario usuario);
    boolean existePorEmail(String email);
}
