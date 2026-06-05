package com.duoc.ms_comunicaciones.infrastructure.adapter.in.rest;

import com.duoc.ms_comunicaciones.domain.model.Canal;
import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.domain.port.in.ComunicacionUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComunicacionController.class)
@AutoConfigureMockMvc(addFilters = false) // Deshabilita Spring Security para el test unitario
class ComunicacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComunicacionUseCase useCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void enviar_DeberiaRetornarComunicacionResponseDTO_CuandoEsExitoso() throws Exception {
        // Arrange
        Comunicacion mockGuardada = Comunicacion.builder()
                .id(1L)
                .usuarioId("USR01")
                .destinatario("test@duoc.cl")
                .asunto("Asunto Test")
                .mensaje("Mensaje Test")
                .tipo("ALERTA")
                .canal(Canal.EMAIL)
                .fechaEnvio(LocalDateTime.now())
                .leido(false)
                .build();

        when(useCase.enviar(any(Comunicacion.class))).thenReturn(mockGuardada);

        String jsonRequest = "{\"usuarioId\":\"USR01\",\"destinatario\":\"test@duoc.cl\",\"asunto\":\"Asunto Test\",\"mensaje\":\"Mensaje Test\",\"canal\":\"EMAIL\",\"tipo\":\"ALERTA\"}";

        // Act & Assert
        mockMvc.perform(post("/api/comunicaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensajeId").value(1L))
                .andExpect(jsonPath("$.canal").value("EMAIL"))
                .andExpect(jsonPath("$.leido").value(false));
    }

    @Test
    void getMensaje_DeberiaRetornar200_CuandoMensajeExiste() throws Exception {
        Comunicacion mockComunicacion = Comunicacion.builder()
                .id(10L)
                .canal(Canal.SMS)
                .build();

        when(useCase.getMensaje(10L)).thenReturn(Optional.of(mockComunicacion));

        mockMvc.perform(get("/api/comunicaciones/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensajeId").value(10L));
    }

    @Test
    void getMensaje_DeberiaRetornar404_CuandoMensajeNoExiste() throws Exception {
        when(useCase.getMensaje(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/comunicaciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBandeja_DeberiaRetornarLista() throws Exception {
        when(useCase.getBandeja("USR01")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/comunicaciones/bandeja/USR01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void marcarLeido_DeberiaRetornarComunicacionActualizada() throws Exception {
        Comunicacion mockActualizada = Comunicacion.builder()
                .id(1L)
                .canal(Canal.EMAIL)
                .leido(true)
                .build();

        when(useCase.marcarLeido(1L)).thenReturn(mockActualizada);

        mockMvc.perform(patch("/api/comunicaciones/leido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leido").value(true));
    }
}