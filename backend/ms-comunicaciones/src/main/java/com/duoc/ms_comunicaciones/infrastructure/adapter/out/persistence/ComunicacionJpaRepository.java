package com.duoc.ms_comunicaciones.infrastructure.adapter.out.persistence;

import com.duoc.ms_comunicaciones.infrastructure.adapter.out.persistence.entity.ComunicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComunicacionJpaRepository extends JpaRepository<ComunicacionEntity, Long> {
    List<ComunicacionEntity> findByUsuarioId(String usuarioId);
}