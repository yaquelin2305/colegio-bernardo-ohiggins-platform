package com.duoc.ms_asistencia.domain.strategy;

import com.duoc.ms_asistencia.domain.model.Asistencia;

public interface AsistenciaStrategy {
    void procesar(Asistencia asistencia);
    boolean aplica(String estado);
}