package cl.duoc.colegio.usuario.domain.port.out;

import cl.duoc.colegio.usuario.domain.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida (driven port) para la persistencia de usuarios.
 *
 * Define el contrato que la capa de Dominio/Aplicación necesita de la BD.
 * La implementación concreta (JPA) vive en Infraestructura.
 */
public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorRut(String rut);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(UUID id);
    List<Usuario> buscarPorRol(String rol);
    Usuario guardar(Usuario usuario);
    boolean existePorRut(String rut);
    boolean existePorEmail(String email);
}
