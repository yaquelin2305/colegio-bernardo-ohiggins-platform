package com.duoc.ms_asistencia.domain.port.in;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import com.duoc.ms_asistencia.domain.model.ResumenAsistencia;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaUseCase {
    List<Asistencia> registrarLista(List<Asistencia> asistencias);
    List<Asistencia> obtenerPorCursoYFecha(String cursoId, LocalDate fecha);
    List<Asistencia> obtenerPorEstudiante(String estudianteId);
    List<Asistencia> obtenerInasistencias();
    ResumenAsistencia obtenerResumen(String cursoId, LocalDate fecha);
    Asistencia justificarInasistencia(Long id, String motivo);
}