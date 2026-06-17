package com.duoc.ms_asistencia.domain.port.out;

import com.duoc.ms_asistencia.domain.model.Anotacion;

import java.util.List;

public interface AnotacionRepositoryPort {
    Anotacion guardar(Anotacion anotacion);
    List<Anotacion> buscarPorEstudiante(String estudianteId);
}
