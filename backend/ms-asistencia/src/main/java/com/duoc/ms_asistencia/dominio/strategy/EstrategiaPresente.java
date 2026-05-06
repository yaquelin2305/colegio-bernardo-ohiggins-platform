package com.duoc.ms_asistencia.dominio.strategy;

import com.duoc.ms_asistencia.dominio.entity.Asistencia;

public class EstrategiaPresente implements EstrategiaAsistencia {
    @Override
    public Asistencia procesar(Asistencia asistencia) {
        asistencia.setObservacion("Asistencia registrada");
        return asistencia;
    }
}