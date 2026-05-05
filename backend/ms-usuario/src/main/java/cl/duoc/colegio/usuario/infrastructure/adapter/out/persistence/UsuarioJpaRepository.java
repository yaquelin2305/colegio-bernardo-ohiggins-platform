package cl.duoc.colegio.usuario.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA Spring Data.
 * Detalle interno de la infraestructura — el dominio NO lo conoce.
 */
@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByRut(String rut);
    Optional<UsuarioEntity> findByEmail(String email);
    List<UsuarioEntity> findByRol(String rol);
    boolean existsByRut(String rut);
    boolean existsByEmail(String email);
}
