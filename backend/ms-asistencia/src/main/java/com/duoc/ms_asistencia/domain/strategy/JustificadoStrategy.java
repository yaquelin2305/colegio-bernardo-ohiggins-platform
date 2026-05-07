package com.duoc.ms_asistencia.domain.strategy;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import org.springframework.stereotype.Component;

@Component
public class JustificadoStrategy implements AsistenciaStrategy {

    @Override
    public void procesar(Asistencia asistencia) {
        System.out.println("LOG: Inasistencia justificada para alumno: " + asistencia.getEstudianteId()
            + " — motivo: " + asistencia.getObservacion());
    }

    @Override
    public boolean aplica(String estado) {
        return "JUSTIFICADO".equalsIgnoreCase(estado);
    }
}
