#set( $symbol_dollar = '$' )
package ${package}.infrastructure.config;

import ${package}.domain.exception.CredencialesInvalidasException;
import ${package}.domain.exception.UsuarioInactivoException;
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

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 401);
        body.put("mensaje", "Credenciales inválidas");
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(UsuarioInactivoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioInactivo(UsuarioInactivoException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 403);
        body.put("mensaje", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });

        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("mensaje", "Error de validación");
        body.put("errores", errors);
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Error inesperado", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("status", 500);
        body.put("mensaje", "Error interno del servidor");
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
