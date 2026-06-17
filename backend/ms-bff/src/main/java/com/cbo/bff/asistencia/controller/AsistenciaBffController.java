package com.cbo.bff.asistencia.controller;

import java.time.LocalDate;
import java.util.List;

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

import com.cbo.bff.asistencia.dto.AlumnoBffDTO;
import com.cbo.bff.asistencia.dto.AnotacionBffDTO;
import com.cbo.bff.asistencia.dto.AsistenciaRequestBffDTO;
import com.cbo.bff.asistencia.dto.EstudianteAsistenciaBffDTO;
import com.cbo.bff.asistencia.dto.HistorialAsistenciaBffDTO;
import com.cbo.bff.asistencia.dto.InasistenciaBffDTO;
import com.cbo.bff.asistencia.dto.JustificacionBffRequestDTO;
import com.cbo.bff.asistencia.dto.ResumenAsistenciaBffDTO;
import com.cbo.bff.asistencia.service.AsistenciaBffService;

import lombok.RequiredArgsConstructor;

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

    @PostMapping("/anotaciones")
    public ResponseEntity<AnotacionBffDTO> guardarAnotacion(@RequestBody AnotacionBffDTO request) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(asistenciaBffService.guardarAnotacion(request));
    }

    @GetMapping("/anotaciones/estudiante/{estudianteId}")
    public ResponseEntity<List<AnotacionBffDTO>> getAnotaciones(@PathVariable String estudianteId) {
        return ResponseEntity.ok(asistenciaBffService.getAnotacionesPorEstudiante(estudianteId));
    }

    @GetMapping("/alumnos/{cursoId}")
    public ResponseEntity<List<AlumnoBffDTO>> getAlumnosPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(asistenciaBffService.getAlumnosPorCurso(cursoId));
    }
}
