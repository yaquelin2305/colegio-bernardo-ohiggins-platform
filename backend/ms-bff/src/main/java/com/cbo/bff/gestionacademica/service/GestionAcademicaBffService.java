package com.cbo.bff.gestionacademica.service;

import com.cbo.bff.asistencia.client.AsistenciaFeignClient;
import com.cbo.bff.asistencia.dto.ms.AsistenciaMsResponseDTO;
import com.cbo.bff.gestionacademica.dto.*;
import com.cbo.bff.gestionacademica.client.AcademicoFeignClient;
import com.cbo.bff.gestionacademica.client.UsuarioFeignClient;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioMsDTO;
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
    private final AsistenciaFeignClient asistenciaFeignClient;

    public BoletinDto obtenerBoletin(UUID estudianteId) {
        List<CalificacionesAcademicoDto> calificaciones =
                academicoFeignClient.obtenerCalificacionesPorEstudiante(estudianteId);

        List<Map<String, Object>> asignaturas = academicoFeignClient.listarAsignaturas();
        Map<Long, String> nombreAsignaturas = asignaturas.stream()
                .collect(java.util.stream.Collectors.toMap(
                        a -> ((Number) a.get("id")).longValue(),
                        a -> (String) a.get("nombre"),
                        (a, b) -> a));

        List<CalificacionResumenDto> resumen = calificaciones.stream()
                .map(c -> CalificacionResumenDto.builder()
                        .asignaturaId(c.getAsignaturaId())
                        .asignaturaNombre(nombreAsignaturas.getOrDefault(
                                c.getAsignaturaId(), "Asignatura #" + c.getAsignaturaId()))
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

        UsuarioMsDTO usuario = usuarioFeignClient.obtenerPorId(estudianteId);
        String nombreCompleto = usuario != null ? usuario.getNombreCompleto() : null;
        String rut = usuario != null ? usuario.getRut() : null;

        // Resolver curso desde matrículas del estudiante
        List<Map<String, Object>> cursos = academicoFeignClient.listarCursos();
        Map<Long, String> nombreCursos = cursos.stream()
                .collect(java.util.stream.Collectors.toMap(
                        c -> ((Number) c.get("id")).longValue(),
                        c -> (String) c.get("nombre"),
                        (a, b) -> a));
        String curso = academicoFeignClient.listarMatriculasPorEstudiante(estudianteId)
                .stream()
                .findFirst()
                .map(m -> nombreCursos.getOrDefault(((Number) m.get("cursoId")).longValue(), null))
                .orElse(null);

        // Calcular porcentaje de asistencia desde registros del estudiante
        List<AsistenciaMsResponseDTO> asistencias = asistenciaFeignClient.getPorEstudiante(estudianteId.toString());
        Double porcentajeAsistencia = null;
        if (asistencias != null && !asistencias.isEmpty()) {
            long presentes = asistencias.stream()
                    .filter(a -> "PRESENTE".equalsIgnoreCase(a.getEstado()) || "JUSTIFICADO".equalsIgnoreCase(a.getEstado()))
                    .count();
            porcentajeAsistencia = Math.round((presentes * 100.0 / asistencias.size()) * 10.0) / 10.0;
        }

        return BoletinDto.builder()
                .estudianteUuid(estudianteId)
                .nombreCompleto(nombreCompleto)
                .rut(rut)
                .curso(curso)
                .calificaciones(resumen)
                .promedioGeneral(promedioRedondeado)
                .porcentajeAsistencia(porcentajeAsistencia)
                .build();
    }

    public List<Map<String, Object>> listarCursos() {
        return academicoFeignClient.listarCursos();
    }

    public List<Map<String, Object>> listarAsignaturas() {
        return academicoFeignClient.listarAsignaturas();
    }

    public List<UsuarioMsDTO> listarUsuariosPorRol(String rol) {
        return usuarioFeignClient.listarPorRol(rol);
    }

    public DashboardStatsDto obtenerStats() {
        List<UsuarioMsDTO> estudiantes = usuarioFeignClient.listarPorRol("ESTUDIANTE");
        List<UsuarioMsDTO> docentes    = usuarioFeignClient.listarPorRol("DOCENTE");
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
