package com.duoc.ms_asistencia.dominio.factory;

import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import com.duoc.ms_asistencia.dominio.entity.EstadoAsistencia;
import java.time.LocalDate;

public class AsistenciaFactory {
    public static Asistencia crearRegistro(EstadoAsistencia estado, Long estudianteId, LocalDate fecha, String observacion) {
        Asistencia asistencia = new Asistencia(estudianteId, fecha, estado, observacion);
        return asistencia;
    }
}