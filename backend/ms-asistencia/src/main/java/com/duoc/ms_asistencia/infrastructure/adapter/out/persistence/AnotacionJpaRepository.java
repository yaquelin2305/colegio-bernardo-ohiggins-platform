package com.duoc.ms_asistencia.infrastructure.adapter.out.persistence;

import com.duoc.ms_asistencia.infrastructure.adapter.out.persistence.entity.AnotacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnotacionJpaRepository extends JpaRepository<AnotacionEntity, Long> {
    List<AnotacionEntity> findByEstudianteId(String estudianteId);
}
