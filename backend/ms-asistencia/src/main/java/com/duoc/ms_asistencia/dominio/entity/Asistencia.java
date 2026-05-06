package com.duoc.ms_asistencia.dominio.entity;

import java.time.LocalDate;

public class Asistencia {
    private Long id;
    private Long estudianteId;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private String observacion;

    public Asistencia() {}

    public Asistencia(Long estudianteId, LocalDate fecha, EstadoAsistencia estado, String observacion) {
        this.estudianteId = estudianteId;
        this.fecha = fecha;
        this.estado = estado;
        this.observacion = observacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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