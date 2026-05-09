package com.cbo.bff.gestionacademica.infrastructure.output.feign;

import com.cbo.bff.gestionacademica.domain.dto.CalificacionesAcademicoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "ms-academico")
public interface AcademicoFeignClient {

    @GetMapping("/api/v1/calificaciones/estudiante/{usuarioUuid}")
    List<CalificacionesAcademicoDto> obtenerCalificacionesPorEstudiante(
            @PathVariable("usuarioUuid") UUID usuarioUuid);

    @GetMapping("/api/v1/cursos")
    List<Map<String, Object>> listarCursos();

    @GetMapping("/api/v1/asignaturas")
    List<Map<String, Object>> listarAsignaturas();
}
