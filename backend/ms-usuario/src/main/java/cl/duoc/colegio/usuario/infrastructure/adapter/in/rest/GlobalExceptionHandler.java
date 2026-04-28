package cl.duoc.colegio.usuario.infrastructure.adapter.in.rest;

import cl.duoc.colegio.usuario.domain.exception.CredencialesInvalidasException;
import cl.duoc.colegio.usuario.domain.exception.EmailYaRegistradoException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioDomainException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioInactivoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el MS-Usuario.
 * Transforma excepciones de dominio en respuestas HTTP apropiadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        // SIEMPRE devolver 401 — nunca revelar si el email existe o no
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, "Credenciales inválidas", LocalDateTime.now()));
    }

    @ExceptionHandler(UsuarioInactivoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioInactivo(UsuarioInactivoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<ErrorResponse> handleEmailYaRegistrado(EmailYaRegistradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(UsuarioDomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(UsuarioDomainException ex) {
        log.warn("Excepción de dominio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("mensaje", "Error de validación");
        response.put("errores", errors);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Error inesperado en MS-Usuario", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Error interno del servidor", LocalDateTime.now()));
    }

    public record ErrorResponse(int status, String mensaje, LocalDateTime timestamp) {}
}
