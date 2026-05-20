package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.domain.model.Usuario;

import java.util.List;
import java.util.UUID;

public interface GestionUsuariosUseCase {

    Usuario obtenerPorId(UUID id);

    List<Usuario> listarPorRol(String rol);

    Usuario actualizar(UUID id, String nombre, String apellido, String email);

    void eliminar(UUID id);

    Usuario obtenerPorRut(String rut);
}
