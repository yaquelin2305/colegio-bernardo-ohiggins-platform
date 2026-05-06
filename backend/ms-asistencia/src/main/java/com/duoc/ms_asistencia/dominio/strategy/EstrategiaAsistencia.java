package com.duoc.ms_asistencia.dominio.strategy;

import com.duoc.ms_asistencia.dominio.entity.Asistencia;

public interface EstrategiaAsistencia {
    Asistencia procesar(Asistencia asistencia);
}