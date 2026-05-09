package com.cbo.bff.asistencia.infrastructure.input.rest;

import com.cbo.bff.asistencia.application.service.AsistenciaBffService;
import com.cbo.bff.asistencia.domain.dto.AsistenciaRequestBffDTO;
import com.cbo.bff.asistencia.domain.dto.EstudianteAsistenciaBffDTO;
import com.cbo.bff.asistencia.domain.dto.HistorialAsistenciaBffDTO;
import com.cbo.bff.asistencia.domain.dto.InasistenciaBffDTO;
import com.cbo.bff.asistencia.domain.dto.JustificacionBffRequestDTO;
import com.cbo.bff.asistencia.domain.dto.ResumenAsistenciaBffDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bff/asistencia")
@RequiredArgsConstructor
public class AsistenciaBffController {

    private final AsistenciaBffService asistenciaBffService;

    @PostMapping("/registrar")
    public ResponseEntity<List<EstudianteAsistenciaBffDTO>> registrar(
            @RequestBody List<AsistenciaRequestBffDTO> request) {
        return ResponseEntity.ok(asistenciaBffService.registrar(request));
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<EstudianteAsistenciaBffDTO>> getPorCurso(
            @PathVariable String cursoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaBffService.getPorCurso(cursoId, fecha));
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<HistorialAsistenciaBffDTO>> getPorEstudiante(
            @PathVariable String estudianteId) {
        return ResponseEntity.ok(asistenciaBffService.getPorEstudiante(estudianteId));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenAsistenciaBffDTO> getResumen(
            @RequestParam String cursoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaBffService.getResumen(cursoId, fecha));
    }

    @GetMapping("/inasistencias")
    public ResponseEntity<List<InasistenciaBffDTO>> getInasistencias() {
        return ResponseEntity.ok(asistenciaBffService.getInasistencias());
    }

    @PatchMapping("/{id}/justificar")
    public ResponseEntity<InasistenciaBffDTO> justificar(
            @PathVariable Long id,
            @RequestBody JustificacionBffRequestDTO request) {
        return ResponseEntity.ok(asistenciaBffService.justificar(id, request));
    }
}
