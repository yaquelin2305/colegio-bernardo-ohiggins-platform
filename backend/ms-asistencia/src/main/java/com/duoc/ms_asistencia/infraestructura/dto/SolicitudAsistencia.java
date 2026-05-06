package com.duoc.ms_asistencia.infraestructura.dto;

import com.duoc.ms_asistencia.dominio.entity.EstadoAsistencia;
import java.time.LocalDate;

public class SolicitudAsistencia {
    private Long estudianteId;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private String observacion;

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}