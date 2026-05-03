package cl.duoc.colegio.usuario.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.usuario.domain.model.RolUsuario;
import cl.duoc.colegio.usuario.domain.model.Usuario;
import cl.duoc.colegio.usuario.domain.port.out.UsuarioRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida: implementa el puerto de repositorio usando JPA.
 *
 * Este es el único lugar donde el dominio "toca" la base de datos.
 * Convierte entre UsuarioEntity (JPA) y Usuario (dominio).
 */
@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> buscarPorRut(String rut) {
        return jpaRepository.findByRut(rut).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public boolean existePorRut(String rut) {
        return jpaRepository.existsByRut(rut);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public List<Usuario> buscarPorRol(String rol) {
        return jpaRepository.findByRol(rol).stream()
                .map(this::toDomain)
                .toList();
    }

    // ── Mappers privados ────────────────────────────────────────────────────────

    private Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getRut(),
                entity.getEmail(),
                entity.getPasswordHash(),
                RolUsuario.valueOf(entity.getRol()),
                entity.getNombre(),
                entity.getApellido(),
                entity.getPerfilId(),
                entity.isActivo(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }

    private UsuarioEntity toEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setRut(usuario.getRut());
        entity.setEmail(usuario.getEmail());
        entity.setPasswordHash(usuario.getPasswordHash());
        entity.setRol(usuario.getRol().name());
        entity.setNombre(usuario.getNombre());
        entity.setApellido(usuario.getApellido());
        entity.setPerfilId(usuario.getPerfilId());
        entity.setActivo(usuario.isActivo());
        entity.setCreadoEn(usuario.getCreadoEn());
        entity.setActualizadoEn(usuario.getActualizadoEn());
        return entity;
    }
}
