package com.colegio.notificacion.infrastructure.persistence;

import com.colegio.notificacion.domain.model.Notificacion;
import com.colegio.notificacion.domain.ports.NotificacionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificacionRepositoryAdapter implements NotificacionRepositoryPort {

    private final JpaNotificacionRepository jpaRepository;

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        NotificacionEntity entity = new NotificacionEntity(
            null, 
            notificacion.getEmisor(),
            notificacion.getDestinatario(),
            notificacion.getAsunto(),
            notificacion.getMensaje(),
            notificacion.getFechaEnvio(),
            notificacion.isLeido()
        );
        NotificacionEntity savedEntity = jpaRepository.save(entity);
        notificacion.setId(savedEntity.getId());
        return notificacion;
    }

    @Override
    public List<Notificacion> obtenerTodas() {
        return jpaRepository.findAll().stream()
            .map(entity -> new Notificacion(
                entity.getId(),
                entity.getEmisor(),
                entity.getDestinatario(),
                entity.getAsunto(),
                entity.getMensaje(),
                entity.getFechaEnvio(),
                entity.isLeido()
            )).collect(Collectors.toList());
    }
}