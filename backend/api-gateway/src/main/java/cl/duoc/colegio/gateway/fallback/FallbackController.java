package cl.duoc.colegio.gateway.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * FALLBACK CONTROLLER — Respuestas de degradación controlada
 *
 * Se activa cuando el Circuit Breaker detecta que un microservicio
 * no está disponible (falla, timeout o circuito abierto).
 *
 * En lugar de propagar un error 500 genérico, retorna:
 *  - HTTP 503 Service Unavailable
 *  - Body RFC 7807 Problem Details (consistente con el resto del sistema)
 *
 * El Gateway redirige a estos endpoints vía:
 *   fallbackUri: forward:/fallback/academico
 */
@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Fallback para MS-Académico (Gestión Académica).
     * Se activa cuando academicoCB (Circuit Breaker) está abierto.
     */
    @GetMapping("/academico")
    public Mono<ResponseEntity<Map<String, Object>>> academicoFallback(
            ServerWebExchange exchange) {

        log.warn("[CIRCUIT BREAKER] MS-Académico no disponible — activando fallback");

        Map<String, Object> body = Map.of(
                "type", "about:blank",
                "title", "Servicio no disponible",
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "detail", "El servicio de Gestión Académica no está disponible en este momento. " +
                          "Por favor, intente nuevamente en unos minutos.",
                "service", "ms-academico",
                "timestamp", Instant.now().toString()
        );

        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body)
        );
    }

    /**
     * Fallback genérico — para cualquier MS sin fallback específico.
     */
    @GetMapping("/default")
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback(
            ServerWebExchange exchange) {

        log.warn("[CIRCUIT BREAKER] Servicio no disponible — fallback genérico activado");

        Map<String, Object> body = Map.of(
                "type", "about:blank",
                "title", "Servicio temporalmente no disponible",
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "detail", "Uno o más servicios no responden. Reintente en unos momentos.",
                "timestamp", Instant.now().toString()
        );

        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body)
        );
    }
}
