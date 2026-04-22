package com.colegio.notificacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaNotificacionRepository extends JpaRepository<NotificacionEntity, Long> {
}