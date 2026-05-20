package com.cbo.bff.calificaciones.controller;

import com.cbo.bff.calificaciones.dto.CalificacionBffResponseDTO;
import com.cbo.bff.calificaciones.dto.GuardarCalificacionBffRequestDTO;
import com.cbo.bff.calificaciones.service.CalificacionesBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff/calificaciones")
@RequiredArgsConstructor
public class CalificacionesBffController {

    private final CalificacionesBffService calificacionesBffService;

    @GetMapping("/curso/{cursoId}/asignatura/{asignaturaId}")
    public ResponseEntity<List<CalificacionBffResponseDTO>> obtenerCalificaciones(
            @PathVariable Long cursoId,
            @PathVariable Long asignaturaId) {
        return ResponseEntity.ok(calificacionesBffService.obtenerCalificaciones(cursoId, asignaturaId));
    }

    @PutMapping("/guardar")
    public ResponseEntity<Void> guardarCalificaciones(
            @RequestBody List<GuardarCalificacionBffRequestDTO> calificaciones) {
        calificacionesBffService.guardarCalificaciones(calificaciones);
        return ResponseEntity.ok().build();
    }
}
