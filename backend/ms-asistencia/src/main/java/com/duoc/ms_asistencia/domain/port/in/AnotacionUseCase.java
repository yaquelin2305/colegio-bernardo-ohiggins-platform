package com.duoc.ms_asistencia.domain.port.in;

import com.duoc.ms_asistencia.domain.model.Anotacion;

import java.util.List;

public interface AnotacionUseCase {
    Anotacion guardar(Anotacion anotacion);
    List<Anotacion> listarPorEstudiante(String estudianteId);
}
