package com.cbo.bff.asistencia.client;

import com.cbo.bff.asistencia.dto.ms.AnotacionMsRequestDTO;
import com.cbo.bff.asistencia.dto.ms.AnotacionMsResponseDTO;
import com.cbo.bff.asistencia.dto.ms.AsistenciaMsRequestDTO;
import com.cbo.bff.asistencia.dto.ms.AsistenciaMsResponseDTO;
import com.cbo.bff.asistencia.dto.ms.JustificacionMsRequestDTO;
import com.cbo.bff.asistencia.dto.ms.ResumenAsistenciaMsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "ms-asistencia")
public interface AsistenciaFeignClient {

    @PostMapping("/api/asistencia/registrar")
    List<AsistenciaMsResponseDTO> registrar(@RequestBody List<AsistenciaMsRequestDTO> request);

    @GetMapping("/api/asistencia/curso/{cursoId}")
    List<AsistenciaMsResponseDTO> getPorCurso(
            @PathVariable String cursoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha);

    @GetMapping("/api/asistencia/estudiante/{estudianteId}")
    List<AsistenciaMsResponseDTO> getPorEstudiante(@PathVariable String estudianteId);

    @GetMapping("/api/asistencia/resumen")
    ResumenAsistenciaMsDTO getResumen(
            @RequestParam String cursoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha);

    @GetMapping("/api/asistencia/inasistencias")
    List<AsistenciaMsResponseDTO> getInasistencias();

    @PatchMapping("/api/asistencia/{id}/justificar")
    AsistenciaMsResponseDTO justificar(
            @PathVariable Long id,
            @RequestBody JustificacionMsRequestDTO request);

    @PostMapping("/api/asistencia/anotaciones")
    AnotacionMsResponseDTO guardarAnotacion(@RequestBody AnotacionMsRequestDTO request);

    @GetMapping("/api/asistencia/anotaciones/estudiante/{estudianteId}")
    List<AnotacionMsResponseDTO> getAnotacionesPorEstudiante(@PathVariable String estudianteId);
}
