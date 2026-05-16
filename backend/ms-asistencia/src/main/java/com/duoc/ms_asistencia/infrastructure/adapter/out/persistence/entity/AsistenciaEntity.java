package com.duoc.ms_asistencia.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "asistencias")
@Data
public class AsistenciaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String estudianteId;
    private String cursoId;
    private LocalDate fecha;
    private String estado;
    private String observacion;
}