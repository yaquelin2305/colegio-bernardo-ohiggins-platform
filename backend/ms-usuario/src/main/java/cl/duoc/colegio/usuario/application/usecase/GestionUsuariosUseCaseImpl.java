package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.domain.exception.UsuarioNoEncontradoException;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.in.GestionUsuariosUseCase;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementación del caso de uso Gestión de Usuarios (CRUD).
 *
 * Administra el ciclo de vida completo de los usuarios:
 * consulta, actualización y eliminación lógica (soft delete).
 *
 * <h3>Transaccionalidad</h3>
 * <ul>
 *   <li>Lecturas: {@code @Transactional(readOnly = true)} — optimiza Hibernate</li>
 *   <li>Escrituras: {@code @Transactional} — garantiza atomicidad</li>
 * </ul>
 *
 * <h3>Soft Delete</h3>
 * El método {@code eliminar()} invoca {@link Usuario#desactivar()} que
 * marca {@code activo = false} en lugar de borrar el registro. Esto:
 * <ul>
 *   <li>Preserva la integridad referencial con otros microservicios</li>
 *   <li>Permite auditoría (el registro sigue existiendo)</li>
 *   <li>Impide que el usuario se autentique nuevamente</li>
 * </ul>
 */
@Service
public class GestionUsuariosUseCaseImpl implements GestionUsuariosUseCase {

    private final UsuarioRepositoryPort repositoryPort;

    public GestionUsuariosUseCaseImpl(UsuarioRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(UUID id) {
        return repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(String rol) {
        return repositoryPort.buscarPorRol(rol);
    }

    @Override
    @Transactional
    public Usuario actualizar(UUID id, String nombre, String apellido, String email) {
        Usuario usuario = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id.toString()));
        usuario.actualizar(nombre, apellido, email);
        return repositoryPort.guardar(usuario);
    }

    @Override
    @Transactional
    public void eliminar(UUID id) {
        Usuario usuario = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id.toString()));
        usuario.desactivar();
        repositoryPort.guardar(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorRut(String rut) {
        return repositoryPort.buscarPorRut(rut)
                .orElseThrow(() -> new UsuarioNoEncontradoException(rut));
    }
}
