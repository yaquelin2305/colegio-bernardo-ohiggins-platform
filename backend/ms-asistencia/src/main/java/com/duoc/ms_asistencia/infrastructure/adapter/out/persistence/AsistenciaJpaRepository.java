package com.duoc.ms_asistencia.infrastructure.adapter.out.persistence;

import com.duoc.ms_asistencia.infrastructure.adapter.out.persistence.entity.AsistenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsistenciaJpaRepository extends JpaRepository<AsistenciaEntity, Long> {
    // Busca por curso y fecha
    List<AsistenciaEntity> findByCursoIdAndFecha(String cursoId, LocalDate fecha);
    
    List<AsistenciaEntity> findByEstudianteId(String estudianteId);
    List<AsistenciaEntity> findByEstadoIn(List<String> estados);
}