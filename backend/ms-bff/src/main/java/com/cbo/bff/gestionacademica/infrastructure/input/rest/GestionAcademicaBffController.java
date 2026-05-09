package com.cbo.bff.gestionacademica.infrastructure.input.rest;

import com.cbo.bff.gestionacademica.application.service.GestionAcademicaBffService;
import com.cbo.bff.gestionacademica.domain.dto.BoletinDto;
import com.cbo.bff.gestionacademica.domain.dto.DashboardStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
