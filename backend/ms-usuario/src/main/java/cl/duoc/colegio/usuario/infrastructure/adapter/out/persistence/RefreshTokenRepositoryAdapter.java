package cl.duoc.colegio.usuario.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.usuario.domain.model.RefreshToken;
import cl.duoc.colegio.usuario.domain.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken guardar(RefreshToken refreshToken) {
        RefreshTokenEntity entity = toEntity(refreshToken);
        jpaRepository.save(entity);
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> buscarVigente(String token) {
        return jpaRepository.findByTokenAndRevocadoFalse(token)
                .map(this::toDomain)
                .filter(RefreshToken::estaVigente);
    }

    @Override
    @Transactional
    public void revocarTodosPorUsuario(UUID usuarioId) {
        jpaRepository.revocarTodosPorUsuario(usuarioId);
    }

    private RefreshToken toDomain(RefreshTokenEntity e) {
        RefreshToken rt = new RefreshToken(e.getToken(), e.getUsuarioId(), e.getExpiracion());
        if (e.isRevocado()) rt.revocar();
        return rt;
    }

    private RefreshTokenEntity toEntity(RefreshToken rt) {
        RefreshTokenEntity e = new RefreshTokenEntity();
        e.setToken(rt.getToken());
        e.setUsuarioId(rt.getUsuarioId());
        e.setExpiracion(rt.getExpiracion());
        e.setRevocado(rt.isRevocado());
        return e;
    }
}
