package cl.duoc.colegio.usuario.infrastructure.adapter.in.rest;

import cl.duoc.colegio.usuario.domain.exception.CredencialesInvalidasException;
import cl.duoc.colegio.usuario.domain.exception.EmailYaRegistradoException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioDomainException;
import cl.duoc.colegio.usuario.domain.exception.UsuarioInactivoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler — Pruebas Unitarias")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("CredencialesInvalidasException → 401 con mensaje genérico")
    void handleCredencialesInvalidas_retorna401() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleCredencialesInvalidas(new CredencialesInvalidasException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().mensaje()).isEqualTo("Credenciales inválidas");
    }

    @Test
    @DisplayName("UsuarioInactivoException → 403 con mensaje de la excepción")
    void handleUsuarioInactivo_retorna403() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleUsuarioInactivo(new UsuarioInactivoException("test@test.cl"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(403);
        assertThat(response.getBody().mensaje()).contains("test@test.cl");
    }

    @Test
    @DisplayName("EmailYaRegistradoException → 409 con mensaje de la excepción")
    void handleEmailYaRegistrado_retorna409() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleEmailYaRegistrado(new EmailYaRegistradoException("duplicado@test.cl"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().mensaje()).contains("duplicado@test.cl");
    }

    @Test
    @DisplayName("UsuarioDomainException → 400 con mensaje de la excepción")
    void handleDomainException_retorna400() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleDomainException(new UsuarioDomainException("Error de dominio"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().mensaje()).isEqualTo("Error de dominio");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException → 400 con mapa de errores")
    void handleValidationErrors_retorna400ConErrores() {
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        List<FieldError> fieldErrors = List.of(
                new FieldError("dto", "rut", "RUT inválido"),
                new FieldError("dto", "email", "Email requerido")
        );
        when(bindingResult.getAllErrors()).thenReturn(new java.util.ArrayList<>(fieldErrors));

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("mensaje")).isEqualTo("Error de validación");

        @SuppressWarnings("unchecked")
        Map<String, String> errores = (Map<String, String>) response.getBody().get("errores");
        assertThat(errores).containsKeys("rut", "email");
    }

    @Test
    @DisplayName("Exception genérica → 500 con mensaje interno")
    void handleGenericException_retorna500() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleGenericException(new RuntimeException("Error fatal"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().mensaje()).isEqualTo("Error interno del servidor");
    }

    @Test
    @DisplayName("ErrorResponse record tiene campos correctos")
    void errorResponse_record_camposCorrectos() {
        GlobalExceptionHandler.ErrorResponse error = new GlobalExceptionHandler.ErrorResponse(
                404, "No encontrado", java.time.LocalDateTime.now()
        );

        assertThat(error.status()).isEqualTo(404);
        assertThat(error.mensaje()).isEqualTo("No encontrado");
        assertThat(error.timestamp()).isNotNull();
    }
}
