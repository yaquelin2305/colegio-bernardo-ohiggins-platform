package com.cbo.bff.config;

import com.cbo.bff.gestionacademica.controller.GestionAcademicaBffController;
import com.cbo.bff.gestionacademica.service.GestionAcademicaBffService;
import feign.Request;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GestionAcademicaBffController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GestionAcademicaBffService service;

    @Test
    void cuandoFeignException404_DeberiaRetornar404ConProblemDetail() throws Exception {
        Request feignRequest = Request.create(
                Request.HttpMethod.GET,
                "http://ms-academico/api/v1/cursos",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
        FeignException ex = FeignException.errorStatus("listarCursos",
                feign.Response.builder()
                        .status(404)
                        .reason("Not Found")
                        .request(feignRequest)
                        .headers(Map.of())
                        .build());

        when(service.listarCursos()).thenThrow(ex);

        mockMvc.perform(get("/api/bff/cursos"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void cuandoExcepcionGenerica_DeberiaRetornar500ConProblemDetail() throws Exception {
        when(service.listarCursos()).thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/api/bff/cursos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Error inesperado"));
    }

    @Test
    void cuandoFeignException503_DeberiaRetornar503() throws Exception {
        Request feignRequest = Request.create(
                Request.HttpMethod.GET,
                "http://ms-academico/api/v1/cursos",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
        FeignException ex = FeignException.errorStatus("listarCursos",
                feign.Response.builder()
                        .status(503)
                        .reason("Service Unavailable")
                        .request(feignRequest)
                        .headers(Map.of())
                        .build());

        when(service.listarCursos()).thenThrow(ex);

        mockMvc.perform(get("/api/bff/cursos"))
                .andExpect(status().isServiceUnavailable());
    }
}
