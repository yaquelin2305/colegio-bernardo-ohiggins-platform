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
     * El MS-Comunicaciones provee asistencia — cuando esté disponible
     * se agrega su Feign Client aquí. Por ahora el campo porcentajeAsistencia
     * retorna null.
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
                        .asignaturaNombre("Asignatura #" + c.getAsignaturaId())
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

        // 4. Obtener nombre completo del estudiante desde MS-Usuario
        String nombreCompleto = usuarioClient
                .listarPorRol("ESTUDIANTE").stream()
                .filter(u -> estudianteId.toString().equals(u.get("id")))
                .findFirst()
                .map(u -> (String) u.get("nombreCompleto"))
                .orElse(null);

        BoletinDto boletin = BoletinDto.builder()
                .estudianteUuid(estudianteId)
                .nombreCompleto(nombreCompleto)
                .calificaciones(resumen)
                .promedioGeneral(promedioRedondeado)
                .porcentajeAsistencia(null) // MS-Comunicaciones pendiente de integración
                .build();

        return ResponseEntity.ok(boletin);
    }

    /**
     * Dashboard: estadísticas agregadas del sistema.
     * Cuenta estudiantes, docentes, cursos y asignaturas desde MS-Usuario y MS-Académico.
     */
    @GetMapping("/dashboard/stats")
    @Operation(summary = "Obtener estadísticas del dashboard administrativo")
    public ResponseEntity<DashboardStatsDto> obtenerStats() {

        List<Map<String, Object>> estudiantes = usuarioClient.listarPorRol("ESTUDIANTE");
        List<Map<String, Object>> docentes    = usuarioClient.listarPorRol("DOCENTE");
        List<Map<String, Object>> cursos      = academicoClient.listarCursos();
        List<Map<String, Object>> asignaturas = academicoClient.listarAsignaturas();

        DashboardStatsDto stats = DashboardStatsDto.builder()
                .totalEstudiantes((long) estudiantes.size())
                .totalDocentes((long) docentes.size())
                .totalCursos((long) cursos.size())
                .totalAsignaturas((long) asignaturas.size())
                .promedioGeneralInstitucion(null) // TODO: requiere endpoint de agregación en MS-Académico
                .build();

        return ResponseEntity.ok(stats);
    }
}
