package cl.duoc.colegio.usuario.application.usecase;

import cl.duoc.colegio.usuario.domain.exception.UsuarioNoEncontradoException;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.in.GestionUsuariosUseCase;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
