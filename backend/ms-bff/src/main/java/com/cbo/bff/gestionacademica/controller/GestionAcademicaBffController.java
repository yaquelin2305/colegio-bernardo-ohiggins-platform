package com.cbo.bff.gestionacademica.controller;

import com.cbo.bff.gestionacademica.service.GestionAcademicaBffService;
import com.cbo.bff.gestionacademica.dto.BoletinDto;
import com.cbo.bff.gestionacademica.dto.DashboardStatsDto;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioMsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bff")
@RequiredArgsConstructor
public class GestionAcademicaBffController {

    private final GestionAcademicaBffService gestionAcademicaBffService;

    @GetMapping("/boletin/{estudianteId}")
    public ResponseEntity<BoletinDto> obtenerBoletin(@PathVariable UUID estudianteId) {
        return ResponseEntity.ok(gestionAcademicaBffService.obtenerBoletin(estudianteId));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDto> obtenerStats() {
        return ResponseEntity.ok(gestionAcademicaBffService.obtenerStats());
    }

    @GetMapping("/cursos")
    public ResponseEntity<List<Map<String, Object>>> listarCursos() {
        return ResponseEntity.ok(gestionAcademicaBffService.listarCursos());
    }

    @GetMapping("/asignaturas")
    public ResponseEntity<List<Map<String, Object>>> listarAsignaturas() {
        return ResponseEntity.ok(gestionAcademicaBffService.listarAsignaturas());
    }

    @GetMapping("/usuarios/{rol}")
    public ResponseEntity<List<UsuarioMsDTO>> listarUsuariosPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(gestionAcademicaBffService.listarUsuariosPorRol(rol));
    }
}
