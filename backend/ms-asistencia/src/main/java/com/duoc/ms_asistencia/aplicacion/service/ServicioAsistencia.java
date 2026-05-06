package com.duoc.ms_asistencia.aplicacion.service;

import com.duoc.ms_asistencia.aplicacion.port.PuertoRepositorioAsistencia;
import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import com.duoc.ms_asistencia.dominio.entity.EstadoAsistencia;
import com.duoc.ms_asistencia.dominio.factory.AsistenciaFactory;
import com.duoc.ms_asistencia.dominio.strategy.EstrategiaAsistencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ServicioAsistencia {
    private final PuertoRepositorioAsistencia repositorio;
    private final Map<EstadoAsistencia, EstrategiaAsistencia> estrategias;

    public ServicioAsistencia(PuertoRepositorioAsistencia repositorio,
                               Map<EstadoAsistencia, EstrategiaAsistencia> estrategias) {
        this.repositorio = repositorio;
        this.estrategias = estrategias;
    }

    public Asistencia registrarAsistencia(Asistencia asistencia) {
        EstadoAsistencia estado = asistencia.getEstado();
        EstrategiaAsistencia estrategia = estrategias.get(estado);
        Asistencia procesada = estrategia.procesar(asistencia);
        return repositorio.guardar(procesada);
    }

    public Asistencia buscarPorId(Long id) {
        return repositorio.buscarPorId(id).orElse(null);
    }

    public Page<Asistencia> buscarPorEstudiante(Long estudianteId, Pageable pageable) {
        return repositorio.buscarPorEstudianteId(estudianteId, pageable);
    }

    public Page<Asistencia> buscarPorFecha(java.time.LocalDate fecha, Pageable pageable) {
        return repositorio.buscarPorFecha(fecha, pageable);
    }
}