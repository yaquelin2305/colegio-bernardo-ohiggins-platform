package com.duoc.ms_asistencia.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "anotaciones")
@Data
public class AnotacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String estudianteId;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
}
