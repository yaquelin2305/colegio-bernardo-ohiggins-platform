package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.domain.exception.UsuarioNoEncontradoException;
import cl.duoc.colegio.usuario.domain.model.Usuario;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada (driving port) — caso de uso Gestión de Usuarios.
 *
 * Operaciones CRUD administrativas sobre usuarios. Solo accesible desde
 * endpoints {@code /api/v1/admin/**} protegidos por el API Gateway (RBAC: ADMIN).
 *
 * <h3>Operaciones</h3>
 * <ul>
 *   <li><b>obtenerPorId / obtenerPorRut</b> — Lectura (readOnly). Lanza {@link UsuarioNoEncontradoException} si no existe.</li>
 *   <li><b>listarPorRol</b> — Filtra usuarios por rol (ADMIN, DOCENTE, etc.)</li>
 *   <li><b>actualizar</b> — Modifica nombre, apellido y email. No cambia RUT ni password.</li>
 *   <li><b>eliminar</b> — Soft delete: marca {@code activo = false}. Preserva integridad referencial.</li>
 * </ul>
 *
 * @implNote El soft delete evita borrar físicamente registros que puedan tener
 *           dependencias en otros microservicios. Los usuarios inactivos no pueden
 *           autenticarse ({@link Usuario#puedeAutenticarse()} retorna false).
 */
public interface GestionUsuariosUseCase {

    Usuario obtenerPorId(UUID id);

    List<Usuario> listarPorRol(String rol);

    Usuario actualizar(UUID id, String nombre, String apellido, String email);

    void eliminar(UUID id);

    Usuario obtenerPorRut(String rut);
}
