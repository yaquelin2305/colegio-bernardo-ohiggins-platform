package cl.duoc.colegio.bff.client;

import cl.duoc.colegio.bff.dto.CalificacionesAcademicoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

/**
 * Feign Client → MS-Académico.
 *
 * name="ms-academico" resuelve via Eureka en producción.
 * url="${feign.academico.url:}" permite sobreescribir en dev local.
 */
@FeignClient(name = "ms-academico", url = "${feign.academico.url:}")
public interface AcademicoFeignClient {

    /**
     * Obtener todas las calificaciones de un estudiante (por usuarioUuid).
     * Ruta: GET /api/v1/calificaciones/estudiante/{usuarioUuid}
     * (Endpoint que necesitamos agregar a MS-Académico — ver nota abajo)
     */
    @GetMapping("/api/v1/calificaciones/estudiante/{usuarioUuid}")
    List<CalificacionesAcademicoDto> obtenerCalificacionesPorEstudiante(
            @PathVariable("usuarioUuid") UUID usuarioUuid);
}
