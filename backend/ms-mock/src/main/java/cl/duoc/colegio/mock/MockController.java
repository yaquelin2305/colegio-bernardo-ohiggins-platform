package cl.duoc.colegio.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
public class MockController {

    // ══════════════════════════════════════════════════
    // MS-COMUNICACIONES endpoints
    // ══════════════════════════════════════════════════

    @GetMapping("/api/comunicaciones/bandeja/{usuarioId}")
    public ResponseEntity<List<Map<String, Object>>> getBandeja(@PathVariable String usuarioId) {
        log.info("[MOCK] GET /api/comunicaciones/bandeja/{}", usuarioId);
        return ResponseEntity.ok(List.of(
                mensaje(1L, "Bienvenida", "Bienvenido a la plataforma del Colegio Bernardo O'Higgins"),
                mensaje(2L, "Reunion apoderados", "Se cita a reunion de apoderados el proximo martes"),
                mensaje(3L, "Notas publicadas", "Las notas del primer trimestre ya estan disponibles")
        ));
    }

    @GetMapping("/api/comunicaciones/{mensajeId}")
    public ResponseEntity<Map<String, Object>> getMensaje(@PathVariable Long mensajeId) {
        log.info("[MOCK] GET /api/comunicaciones/{}", mensajeId);
        return ResponseEntity.ok(mensaje(mensajeId, "Mensaje #" + mensajeId, "Contenido de ejemplo para mensaje " + mensajeId));
    }

    @PostMapping("/api/comunicaciones/enviar")
    public ResponseEntity<Map<String, Object>> enviar(@RequestBody Map<String, Object> request) {
        log.info("[MOCK] POST /api/comunicaciones/enviar — asunto: {}", request.get("asunto"));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                mensaje(100L, (String) request.getOrDefault("asunto", "Sin asunto"),
                        (String) request.getOrDefault("mensaje", "")));
    }

    @GetMapping("/api/comunicaciones/destinatarios")
    public ResponseEntity<List<String>> getDestinatarios() {
        log.info("[MOCK] GET /api/comunicaciones/destinatarios");
        return ResponseEntity.ok(List.of("1A", "2B", "3C", "4A", "5B", "6C", "7A", "8B"));
    }

    @PatchMapping("/api/comunicaciones/leido/{mensajeId}")
    public ResponseEntity<Map<String, Object>> marcarLeido(@PathVariable Long mensajeId) {
        log.info("[MOCK] PATCH /api/comunicaciones/leido/{}", mensajeId);
        Map<String, Object> m = mensaje(mensajeId, "Mensaje leido", "Mensaje marcado como leido");
        m.put("leido", true);
        return ResponseEntity.ok(m);
    }

    // ══════════════════════════════════════════════════
    // MS-ASISTENCIA endpoints
    // ══════════════════════════════════════════════════

    @PostMapping("/api/asistencia/registrar")
    public ResponseEntity<List<Map<String, Object>>> registrar(@RequestBody List<Map<String, Object>> request) {
        log.info("[MOCK] POST /api/asistencia/registrar — {} registros", request.size());
        List<Map<String, Object>> result = new ArrayList<>();
        long id = 1;
        for (Map<String, Object> r : request) {
            result.add(asistencia(id++, (String) r.get("estudianteId"), (String) r.get("cursoId"),
                    r.get("fecha") != null ? LocalDate.parse(r.get("fecha").toString()) : LocalDate.now(),
                    (String) r.getOrDefault("estado", "PRESENTE"),
                    (String) r.getOrDefault("observacion", "")));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/api/asistencia/curso/{cursoId}")
    public ResponseEntity<List<Map<String, Object>>> getPorCurso(
            @PathVariable String cursoId,
            @RequestParam(required = false) String fecha) {
        log.info("[MOCK] GET /api/asistencia/curso/{}", cursoId);
        return ResponseEntity.ok(List.of(
                asistencia(1L, "1", cursoId, LocalDate.now(), "PRESENTE", ""),
                asistencia(2L, "2", cursoId, LocalDate.now(), "PRESENTE", ""),
                asistencia(3L, "3", cursoId, LocalDate.now(), "AUSENTE", ""),
                asistencia(4L, "4", cursoId, LocalDate.now(), "JUSTIFICADO", "Enfermedad")
        ));
    }

    @GetMapping("/api/asistencia/estudiante/{estudianteId}")
    public ResponseEntity<List<Map<String, Object>>> getPorEstudiante(@PathVariable String estudianteId) {
        log.info("[MOCK] GET /api/asistencia/estudiante/{}", estudianteId);
        return ResponseEntity.ok(List.of(
                asistencia(1L, estudianteId, "1A", LocalDate.now().minusDays(3), "PRESENTE", ""),
                asistencia(2L, estudianteId, "1A", LocalDate.now().minusDays(2), "PRESENTE", ""),
                asistencia(3L, estudianteId, "1A", LocalDate.now().minusDays(1), "AUSENTE", "")
        ));
    }

    @GetMapping("/api/asistencia/resumen")
    public ResponseEntity<Map<String, Object>> getResumen(
            @RequestParam String cursoId,
            @RequestParam(required = false) String fecha) {
        log.info("[MOCK] GET /api/asistencia/resumen — curso: {}", cursoId);
        return ResponseEntity.ok(Map.of(
                "cursoId", cursoId,
                "fecha", fecha != null ? fecha : LocalDate.now().toString(),
                "totalPresentes", 28,
                "totalAusentes", 3,
                "totalJustificados", 1,
                "total", 32,
                "porcentajeAsistencia", 87.5
        ));
    }

    @GetMapping("/api/asistencia/inasistencias")
    public ResponseEntity<List<Map<String, Object>>> getInasistencias() {
        log.info("[MOCK] GET /api/asistencia/inasistencias");
        return ResponseEntity.ok(List.of(
                asistencia(10L, "5", "2B", LocalDate.now().minusDays(1), "AUSENTE", ""),
                asistencia(11L, "7", "3C", LocalDate.now().minusDays(2), "JUSTIFICADO", "Medico")
        ));
    }

    @PatchMapping("/api/asistencia/{id}/justificar")
    public ResponseEntity<Map<String, Object>> justificar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        log.info("[MOCK] PATCH /api/asistencia/{}/justificar — motivo: {}", id, request.get("motivo"));
        Map<String, Object> a = asistencia(id, "X", "X", LocalDate.now(), "JUSTIFICADO",
                (String) request.getOrDefault("motivo", ""));
        a.put("id", id);
        return ResponseEntity.ok(a);
    }

    // ══════════════════════════════════════════════════
    // Helpers
    // ══════════════════════════════════════════════════

    private Map<String, Object> mensaje(Long id, String asunto, String cuerpo) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("mensajeId", id);
        m.put("usuarioId", "admin");
        m.put("destinatario", "1A");
        m.put("asunto", asunto);
        m.put("mensaje", cuerpo);
        m.put("canal", "PLATAFORMA");
        m.put("tipo", "INFORMATIVO");
        m.put("fechaEnvio", LocalDateTime.now().toString());
        m.put("leido", false);
        return m;
    }

    private Map<String, Object> asistencia(Long id, String estudianteId, String cursoId,
                                            LocalDate fecha, String estado, String observacion) {
        Map<String, Object> a = new LinkedHashMap<>();
        a.put("id", id);
        a.put("estudianteId", estudianteId);
        a.put("cursoId", cursoId);
        a.put("fecha", fecha.toString());
        a.put("estado", estado);
        a.put("observacion", observacion);
        return a;
    }
}
