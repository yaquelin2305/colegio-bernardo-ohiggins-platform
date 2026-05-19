package com.duoc.ms_asistencia.application.service;

import com.duoc.ms_asistencia.domain.model.Anotacion;
import com.duoc.ms_asistencia.domain.port.in.AnotacionUseCase;
import com.duoc.ms_asistencia.domain.port.out.AnotacionRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnotacionService implements AnotacionUseCase {

    private final AnotacionRepositoryPort repositoryPort;

    public AnotacionService(AnotacionRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Anotacion guardar(Anotacion anotacion) {
        if (anotacion.getFecha() == null) {
            anotacion.setFecha(LocalDate.now());
        }
        return repositoryPort.guardar(anotacion);
    }

    @Override
    public List<Anotacion> listarPorEstudiante(String estudianteId) {
        return repositoryPort.buscarPorEstudiante(estudianteId);
    }
}
