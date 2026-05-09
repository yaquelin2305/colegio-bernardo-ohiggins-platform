package com.cbo.bff.gestionacademica.application.service;

import com.cbo.bff.gestionacademica.domain.dto.*;
import com.cbo.bff.gestionacademica.infrastructure.output.feign.AcademicoFeignClient;
import com.cbo.bff.gestionacademica.infrastructure.output.feign.UsuarioFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GestionAcademicaBffService {

    private final AcademicoFeignClient academicoFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;

    public BoletinDto obtenerBoletin(UUID estudianteId) {
        List<CalificacionesAcademicoDto> calificaciones =
                academicoFeignClient.obtenerCalificacionesPorEstudiante(estudianteId);

        List<CalificacionResumenDto> resumen = calificaciones.stream()
                .map(c -> CalificacionResumenDto.builder()
                        .asignaturaId(c.getAsignaturaId())
                        .asignaturaNombre("Asignatura #" + c.getAsignaturaId())
                        .nota1(c.getNota1())
                        .nota2(c.getNota2())
                        .nota3(c.getNota3())
                        .promedio(c.getPromedio())
                        .build())
                .toList();

        double promedioGeneral = calificaciones.stream()
                .filter(c -> c.getPromedio() != null)
                .mapToDouble(CalificacionesAcademicoDto::getPromedio)
                .average()
                .orElse(0.0);

        double promedioRedondeado = Math.round(promedioGeneral * 10.0) / 10.0;

        String nombreCompleto = usuarioFeignClient.listarPorRol("ESTUDIANTE").stream()
                .filter(u -> estudianteId.toString().equals(u.get("id")))
                .findFirst()
                .map(u -> (String) u.get("nombreCompleto"))
                .orElse(null);

        return BoletinDto.builder()
                .estudianteUuid(estudianteId)
                .nombreCompleto(nombreCompleto)
                .calificaciones(resumen)
                .promedioGeneral(promedioRedondeado)
                .porcentajeAsistencia(null)
                .build();
    }

    public DashboardStatsDto obtenerStats() {
        List<Map<String, Object>> estudiantes = usuarioFeignClient.listarPorRol("ESTUDIANTE");
        List<Map<String, Object>> docentes    = usuarioFeignClient.listarPorRol("DOCENTE");
        List<Map<String, Object>> cursos      = academicoFeignClient.listarCursos();
        List<Map<String, Object>> asignaturas = academicoFeignClient.listarAsignaturas();

        return DashboardStatsDto.builder()
                .totalEstudiantes((long) estudiantes.size())
                .totalDocentes((long) docentes.size())
                .totalCursos((long) cursos.size())
                .totalAsignaturas((long) asignaturas.size())
                .promedioGeneralInstitucion(null)
                .build();
    }
}
