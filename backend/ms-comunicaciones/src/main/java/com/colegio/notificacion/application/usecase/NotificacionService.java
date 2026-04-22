package com.colegio.notificacion.application.usecase;

import com.colegio.notificacion.domain.model.Notificacion;
import com.colegio.notificacion.domain.ports.NotificacionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {
    private final NotificacionRepositoryPort repositoryPort;

    public Notificacion crearNotificacion(Notificacion notificacion) {
       
        return repositoryPort.guardar(notificacion);
    }

    public List<Notificacion> listarNotificaciones() {
        return repositoryPort.obtenerTodas();
    }
}