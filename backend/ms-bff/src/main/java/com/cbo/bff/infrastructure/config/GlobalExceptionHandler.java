package com.cbo.bff.infrastructure.config;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(FeignException.class)
    public ProblemDetail handleFeign(FeignException ex) {
        log.warn("[BFF] Error llamando a microservicio — status: {}, url: {}", ex.status(), ex.request().url());

        HttpStatus status = ex.status() >= 400 && ex.status() < 600
                ? HttpStatus.valueOf(ex.status())
                : HttpStatus.BAD_GATEWAY;

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status,
                "Error al comunicarse con el servicio: " + ex.request().url());
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("upstreamStatus", ex.status());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("[BFF] Error inesperado", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() != null ? ex.getMessage() : "Error interno"
        );
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
