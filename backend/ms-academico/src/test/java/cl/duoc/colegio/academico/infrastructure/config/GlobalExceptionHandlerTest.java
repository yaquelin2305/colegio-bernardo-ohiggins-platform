package cl.duoc.colegio.academico.infrastructure.config;

import cl.duoc.colegio.academico.domain.exception.GradeNotFoundException;
import cl.duoc.colegio.academico.domain.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler — Pruebas Unitarias")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFound_student_retorna404() {
        ProblemDetail pd = handler.handleNotFound(new StudentNotFoundException(99L));

        assertThat(pd.getStatus()).isEqualTo(404);
        assertThat(pd.getTitle()).isEqualTo("Recurso no encontrado");
        assertThat(pd.getDetail()).contains("99");
    }

    @Test
    void handleNotFound_grade_retorna404() {
        ProblemDetail pd = handler.handleNotFound(new GradeNotFoundException(50L));

        assertThat(pd.getStatus()).isEqualTo(404);
        assertThat(pd.getDetail()).contains("50");
    }

    @Test
    void handleIllegalArgument_retorna400() {
        ProblemDetail pd = handler.handleIllegalArgument(new IllegalArgumentException("nota inválida"));

        assertThat(pd.getStatus()).isEqualTo(400);
        assertThat(pd.getTitle()).isEqualTo("Argumento inválido");
        assertThat(pd.getDetail()).isEqualTo("nota inválida");
    }

    @Test
    void handleValidation_retorna400ConErrores() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(br);
        when(br.getFieldErrors()).thenReturn(List.of(
                new FieldError("obj", "nombre", "requerido"),
                new FieldError("obj", "curso", "inválido")
        ));

        ProblemDetail pd = handler.handleValidation(ex);

        assertThat(pd.getStatus()).isEqualTo(400);
        assertThat(pd.getTitle()).isEqualTo("Error de validación");
        assertThat(pd.getDetail()).contains("nombre", "curso");
    }

    @Test
    void handleResponseStatus_retornaStatusCode() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.CONFLICT, "conflicto");

        ProblemDetail pd = handler.handleResponseStatus(ex);

        assertThat(pd.getStatus()).isEqualTo(409);
        assertThat(pd.getTitle()).isEqualTo("Error de solicitud");
        assertThat(pd.getDetail()).isEqualTo("conflicto");
    }

    @Test
    void handleGeneral_retorna500() {
        ProblemDetail pd = handler.handleGeneral(new RuntimeException("fatal"));

        assertThat(pd.getStatus()).isEqualTo(500);
        assertThat(pd.getTitle()).isEqualTo("Error interno");
        assertThat(pd.getDetail()).isEqualTo("Error interno del servidor");
    }
}
