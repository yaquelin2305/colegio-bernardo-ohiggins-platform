package com.duoc.ms_comunicaciones.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. Le decimos a WebMvcTest que monte explícitamente el controlador de pruebas de abajo
@WebMvcTest(ConfigFakeController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void filtrarPeticiones_DeberiaPermitirAcceso_CuandoRutaEsPublicaDeComunicaciones() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/comunicaciones/test-publico")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void filtrarPeticiones_DeberiaPermitirAcceso_CuandoRutaEsDeActuator() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/actuator/test-health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void filtrarPeticiones_DeberiaPermitirAcceso_CuandoRutaEsPrivadaTrustGateway() throws Exception {
        mockMvc.perform(get("/api/ruta-privada-protegida")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void procesarCors_DeberiaIncluirCabecerasCors_CuandoOrigenEsValido() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(options("/api/comunicaciones/test-publico")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }
}

// 2. Al estar fuera de las llaves y sin "public", Spring Boot lo mapea de forma perfecta y no tira 404
@RestController
class ConfigFakeController {

    @GetMapping("/api/comunicaciones/test-publico")
    public String publico() {
        return "OK";
    }

    @GetMapping("/actuator/test-health")
    public String health() {
        return "OK";
    }

    @GetMapping("/api/ruta-privada-protegida")
    public String privada() {
        return "OK";
    }
}