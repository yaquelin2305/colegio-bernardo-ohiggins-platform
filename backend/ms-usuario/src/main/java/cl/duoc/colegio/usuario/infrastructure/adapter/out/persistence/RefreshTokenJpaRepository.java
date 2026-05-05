package cl.duoc.colegio.usuario.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByTokenAndRevocadoFalse(String token);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revocado = true WHERE r.usuarioId = :usuarioId")
    void revocarTodosPorUsuario(UUID usuarioId);
}
