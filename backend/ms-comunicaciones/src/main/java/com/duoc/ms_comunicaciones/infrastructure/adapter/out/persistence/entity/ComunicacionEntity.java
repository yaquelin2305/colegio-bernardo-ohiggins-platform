package com.duoc.ms_comunicaciones.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "comunicaciones")
@Data
public class ComunicacionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private String usuarioId;

    private String destinatario;
    private String asunto;
    
    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    private CanalEntity canal; 

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    private boolean leido;
}