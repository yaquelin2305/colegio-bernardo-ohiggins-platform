package com.duoc.ms_asistencia.aplicacion.port;

import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface PuertoRepositorioAsistencia {
    Asistencia guardar(Asistencia asistencia);
    Optional<Asistencia> buscarPorId(Long id);
    Page<Asistencia> buscarPorEstudianteId(Long estudianteId, Pageable pageable);
    Page<Asistencia> buscarPorFecha(java.time.LocalDate fecha, Pageable pageable);
}