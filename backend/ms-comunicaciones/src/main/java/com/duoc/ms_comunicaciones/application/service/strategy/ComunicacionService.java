package com.duoc.ms_comunicaciones.application.service.strategy;

import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.domain.port.in.ComunicacionUseCase;
import com.duoc.ms_comunicaciones.domain.port.in.ComunicacionStrategy;
import com.duoc.ms_comunicaciones.domain.port.out.ComunicacionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComunicacionService implements ComunicacionUseCase {

    private final ComunicacionRepositoryPort repositoryPort;
    private final List<ComunicacionStrategy> strategies;

    public ComunicacionService(ComunicacionRepositoryPort repositoryPort, List<ComunicacionStrategy> strategies) {
        this.repositoryPort = repositoryPort;
        this.strategies = strategies;
    }

    @Override
    public Comunicacion enviar(Comunicacion comunicacion) {
        // Buscamos la estrategia que soporte el canal (EMAIL o SMS)
        ComunicacionStrategy strategy = strategies.stream()
                .filter(s -> s.supports(comunicacion.getCanal()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Canal no soportado"));

        // Despachamos el mensaje
        strategy.dispatch(comunicacion);

        // Guardamos en la base de datos a través del puerto de salida
        return repositoryPort.save(comunicacion);
    }

    @Override
    public List<Comunicacion> getBandeja(String usuarioId) {
        return repositoryPort.findByUsuarioId(usuarioId);
    }

    @Override
    public Optional<Comunicacion> getMensaje(Long mensajeId) {
        return repositoryPort.findById(mensajeId);
    }

    @Override
    public Comunicacion marcarLeido(Long mensajeId) {
        return repositoryPort.updateLeido(mensajeId, true);
    }
}