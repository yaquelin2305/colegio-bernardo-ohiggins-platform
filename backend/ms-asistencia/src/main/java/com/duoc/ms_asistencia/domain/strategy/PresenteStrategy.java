package com.duoc.ms_asistencia.domain.strategy;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import org.springframework.stereotype.Component;

@Component
public class PresenteStrategy implements AsistenciaStrategy {

    @Override
    public void procesar(Asistencia asistencia) {
        System.out.println("LOG: Presencia registrada para alumno: " + asistencia.getEstudianteId());
    }

    @Override
    public boolean aplica(String estado) {
        return "PRESENTE".equalsIgnoreCase(estado);
    }
}
