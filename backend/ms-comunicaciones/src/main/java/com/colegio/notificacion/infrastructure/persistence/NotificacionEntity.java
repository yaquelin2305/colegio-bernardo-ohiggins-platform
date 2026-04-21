package com.colegio.notificacion.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String emisor;
    private String destinatario;
    private String asunto;
    
    @Column(columnDefinition = "TEXT")
    private String mensaje;
    
    private LocalDateTime fechaEnvio;
    private boolean leido;
}