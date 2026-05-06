package com.duoc.ms_asistencia.dominio.strategy;

import com.duoc.ms_asistencia.dominio.entity.Asistencia;

public class EstrategiaAusente implements EstrategiaAsistencia {
    @Override
    public Asistencia procesar(Asistencia asistencia) {
        asistencia.setObservacion("Falta registrada sin justificar");
        return asistencia;
    }
}