package com.duoc.ms_asistencia.domain.strategy;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import org.springframework.stereotype.Component;

@Component
public class AusenciaStrategy implements AsistenciaStrategy {
    @Override
    public void procesar(Asistencia asistencia) {
        // Lógica: Por ejemplo, preparar una notificación para el apoderado
        System.out.println("LOG: Procesando inasistencia del alumno: " + asistencia.getEstudianteId());
    }

    @Override
    public boolean aplica(String estado) {
        return "AUSENTE".equalsIgnoreCase(estado);
    }
}