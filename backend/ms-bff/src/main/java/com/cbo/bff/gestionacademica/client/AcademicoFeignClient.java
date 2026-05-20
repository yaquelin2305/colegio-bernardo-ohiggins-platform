package com.cbo.bff.gestionacademica.client;

import com.cbo.bff.calificaciones.dto.ms.CalificacionMsResponseDTO;
import com.cbo.bff.calificaciones.dto.ms.GuardarCalificacionMsRequestDTO;
import com.cbo.bff.gestionacademica.dto.CalificacionesAcademicoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "ms-academico")
public interface AcademicoFeignClient {

    @GetMapping("/api/v1/calificaciones/estudiante/{usuarioUuid}")
    List<CalificacionesAcademicoDto> obtenerCalificacionesPorEstudiante(
            @PathVariable("usuarioUuid") UUID usuarioUuid);

    @GetMapping("/api/v1/calificaciones/curso/{cursoId}/asignatura/{asignaturaId}")
    List<CalificacionMsResponseDTO> obtenerCalificacionesPorCursoYAsignatura(
            @PathVariable("cursoId") Long cursoId,
            @PathVariable("asignaturaId") Long asignaturaId);

    @PutMapping("/api/v1/calificaciones/guardar")
    void guardarCalificacion(@RequestBody GuardarCalificacionMsRequestDTO request);

    @GetMapping("/api/v1/matriculas/estudiante/{uuid}")
    List<Map<String, Object>> listarMatriculasPorEstudiante(@PathVariable("uuid") UUID uuid);

    @GetMapping("/api/v1/matriculas/curso/{cursoId}/estudiantes")
    List<Map<String, Object>> obtenerEstudiantesPorCurso(@PathVariable("cursoId") Long cursoId);

    @GetMapping("/api/v1/cursos")
    List<Map<String, Object>> listarCursos();

    @GetMapping("/api/v1/asignaturas")
    List<Map<String, Object>> listarAsignaturas();
}
