package com.duoc.ms_asistencia.infraestructura.controller;

import com.duoc.ms_asistencia.aplicacion.service.ServicioAsistencia;
import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import com.duoc.ms_asistencia.dominio.entity.EstadoAsistencia;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ControladorAsistenciaTest {

    private MockMvc mockMvc;
    private ServicioAsistencia servicioAsistencia;
    private ControladorAsistencia controlador;

    @BeforeEach
    void setUp() {
        servicioAsistencia = Mockito.mock(ServicioAsistencia.class);
        controlador = new ControladorAsistencia(servicioAsistencia);
        mockMvc = MockMvcBuilders.standaloneSetup(controlador).build();
    }

    @Test
    void testRegistrarAsistencia_Exitoso() throws Exception {
        // Given
        String jsonRequest = "{\"estudianteId\":1,\"fecha\":\"2026-04-24\",\"estado\":\"PRESENTE\",\"observacion\":\"Asistencia normal\"}";
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), EstadoAsistencia.PRESENTE, "Asistencia registrada");
        asistencia.setId(1L);
        when(servicioAsistencia.registrarAsistencia(any())).thenReturn(asistencia);

        // When & Then
        mockMvc.perform(post("/asistencia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PRESENTE"));
    }

    @Test
    void testBuscarPorId_Existente() throws Exception {
        // Given
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), EstadoAsistencia.PRESENTE, "Asistencia registrada");
        asistencia.setId(1L);
        when(servicioAsistencia.buscarPorId(1L)).thenReturn(asistencia);

        // When & Then
        mockMvc.perform(get("/asistencia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testBuscarPorId_Inexistente() throws Exception {
        // Given
        when(servicioAsistencia.buscarPorId(99L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/asistencia/99"))
                .andExpect(status().isNotFound());
    }
}