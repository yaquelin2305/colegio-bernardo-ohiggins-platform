package com.colegio.notificacion.domain.ports;

import com.colegio.notificacion.domain.model.Notificacion;
import java.util.List;

public interface NotificacionRepositoryPort {
    Notificacion guardar(Notificacion notificacion);
    List<Notificacion> obtenerTodas();
}