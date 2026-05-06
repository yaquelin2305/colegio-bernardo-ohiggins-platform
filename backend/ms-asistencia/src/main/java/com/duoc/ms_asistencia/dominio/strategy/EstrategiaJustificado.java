package com.duoc.ms_asistencia.dominio.strategy;

import com.duoc.ms_asistencia.dominio.entity.Asistencia;

public class EstrategiaJustificado implements EstrategiaAsistencia {
    @Override
    public Asistencia procesar(Asistencia asistencia) {
        String observacion = asistencia.getObservacion();
        if (observacion == null || observacion.isBlank()) {
            observacion = "Falta justificada";
        } else {
            observacion = "Falta justificada: " + observacion;
        }
        asistencia.setObservacion(observacion);
        return asistencia;
    }
}