package com.duoc.ms_asistencia.infraestructura.adapter;

import com.duoc.ms_asistencia.infraestructura.entity.AsistenciaJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RepositorioJpaAsistencia extends JpaRepository<AsistenciaJpa, Long> {
    Page<AsistenciaJpa> findByEstudianteId(Long estudianteId, Pageable pageable);
    Page<AsistenciaJpa> findByFecha(LocalDate fecha, Pageable pageable);
}