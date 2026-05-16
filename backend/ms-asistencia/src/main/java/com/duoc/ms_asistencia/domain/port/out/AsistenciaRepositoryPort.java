package com.duoc.ms_asistencia.domain.port.out;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepositoryPort {
    List<Asistencia> saveAll(List<Asistencia> asistencias);
    Asistencia save(Asistencia asistencia);
    List<Asistencia> findByCursoIdAndFecha(String cursoId, LocalDate fecha);
    List<Asistencia> findByEstudianteId(String estudianteId);
    List<Asistencia> findByEstadoIn(List<String> estados);
    Optional<Asistencia> findById(Long id);
}