package com.duoc.ms_asistencia.infraestructura.controller;

import com.duoc.ms_asistencia.aplicacion.service.ServicioAsistencia;
import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import com.duoc.ms_asistencia.infraestructura.dto.SolicitudAsistencia;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asistencia")
public class ControladorAsistencia {
    private final ServicioAsistencia servicioAsistencia;

    public ControladorAsistencia(ServicioAsistencia servicioAsistencia) {
        this.servicioAsistencia = servicioAsistencia;
    }

    @PostMapping
    public ResponseEntity<Asistencia> registrarAsistencia(@RequestBody SolicitudAsistencia request) {
        Asistencia asistencia = new Asistencia(
                request.getEstudianteId(),
                request.getFecha(),
                request.getEstado(),
                request.getObservacion()
        );
        Asistencia resultado = servicioAsistencia.registrarAsistencia(asistencia);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> buscarPorId(@PathVariable Long id) {
        Asistencia resultado = servicioAsistencia.buscarPorId(id);
        if (resultado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resultado);
    }
}