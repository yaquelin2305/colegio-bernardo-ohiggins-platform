package cl.duoc.colegio.usuario.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA Spring Data.
 * Detalle interno de la infraestructura — el dominio NO lo conoce.
 */
@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
