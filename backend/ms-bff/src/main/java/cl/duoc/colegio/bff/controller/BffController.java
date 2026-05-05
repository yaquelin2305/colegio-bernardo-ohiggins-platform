package cl.duoc.colegio.bff.controller;

import cl.duoc.colegio.bff.client.AcademicoFeignClient;
import cl.duoc.colegio.bff.client.UsuarioFeignClient;
import cl.duoc.colegio.bff.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * BFF Controller — Orquestador de endpoints compuestos.
 *
 * Base: /api/bff
 *
 * Rutas:
 *  GET /api/bff/boletin/{estudianteId}  → Notas + asistencia unificadas
 *  GET /api/bff/dashboard/stats         → Estadísticas del sistema
 */
@RestController
@RequestMapping("/api/bff")
@Tag(name = "BFF", description = "Orquestador Backend for Frontend")
public class BffController {

    private final AcademicoFeignClient academicoClient;
    private final UsuarioFeignClient usuarioClient;

    public BffController(AcademicoFeignClient academicoClient,
                         UsuarioFeignClient usuarioClient) {
        this.academicoClient = academicoClient;
        this.usuarioClient = usuarioClient;
    }

    /**
     * Boletín del estudiante: unifica calificaciones (MS-Académico).
     *
     * El MS-Comunicaciones (MS-2) provee asistencia — cuando esté disponible
     * se agrega su Feign Client aquí. Por ahora el campo porcentajeAsistencia
     * retorna null con una nota en el response.
     *
     * @param estudianteId UUID del estudiante (de MS-Usuario)
     */
    @GetMapping("/boletin/{estudianteId}")
    @Operation(summary = "Obtener boletín completo de un estudiante (notas + asistencia)")
    public ResponseEntity<BoletinDto> obtenerBoletin(
            @PathVariable UUID estudianteId) {

        // 1. Obtener calificaciones desde MS-Académico
        List<CalificacionesAcademicoDto> calificaciones =
                academicoClient.obtenerCalificacionesPorEstudiante(estudianteId);

        // 2. Mapear a resumen por asignatura
        List<CalificacionResumenDto> resumen = calificaciones.stream()
                .map(c -> CalificacionResumenDto.builder()
                        .asignaturaId(c.getAsignaturaId())
                        .asignaturaNombre("Asignatura #" + c.getAsignaturaId()) // enriquecible si hay endpoint
                        .nota1(c.getNota1())
                        .nota2(c.getNota2())
                        .nota3(c.getNota3())
                        .promedio(c.getPromedio())
                        .build())
                .toList();

        // 3. Calcular promedio general (promedio de promedios por asignatura)
        double promedioGeneral = calificaciones.stream()
                .filter(c -> c.getPromedio() != null)
                .mapToDouble(CalificacionesAcademicoDto::getPromedio)
                .average()
                .orElse(0.0);

        double promedioRedondeado = Math.round(promedioGeneral * 10.0) / 10.0;

        BoletinDto boletin = BoletinDto.builder()
                .estudianteUuid(estudianteId)
                .calificaciones(resumen)
                .promedioGeneral(promedioRedondeado)
                .porcentajeAsistencia(null) // MS-Comunicaciones pendiente de integración
                .build();

        return ResponseEntity.ok(boletin);
    }

    /**
     * Dashboard: estadísticas agregadas del sistema.
     * Cuenta estudiantes, docentes y apoderados desde MS-Usuario.
     */
    @GetMapping("/dashboard/stats")
    @Operation(summary = "Obtener estadísticas del dashboard administrativo")
    public ResponseEntity<DashboardStatsDto> obtenerStats() {

        List<Map<String, Object>> estudiantes = usuarioClient.listarPorRol("ESTUDIANTE");
        List<Map<String, Object>> docentes    = usuarioClient.listarPorRol("DOCENTE");

        DashboardStatsDto stats = DashboardStatsDto.builder()
                .totalEstudiantes((long) estudiantes.size())
                .totalDocentes((long) docentes.size())
                .build();

        return ResponseEntity.ok(stats);
    }
}
