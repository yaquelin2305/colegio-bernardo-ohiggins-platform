package com.cbo.bff.calificaciones.controller;

import com.cbo.bff.calificaciones.dto.CalificacionBffResponseDTO;
import com.cbo.bff.calificaciones.service.CalificacionesBffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CalificacionesBffController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class CalificacionesBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalificacionesBffService service;

    @Test
    void obtenerCalificaciones_DeberiaRetornar200ConLista() throws Exception {
        CalificacionBffResponseDTO dto = CalificacionBffResponseDTO.builder()
                .id("uuid-01").nombre("Sofía Morales")
                .nota1(6.0).nota2(5.5).nota3(4.0).promedio(5.2)
                .build();

        when(service.obtenerCalificaciones(1L, 2L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/bff/calificaciones/curso/1/asignatura/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Sofía Morales"))
                .andExpect(jsonPath("$[0].nota1").value(6.0))
                .andExpect(jsonPath("$[0].promedio").value(5.2));
    }

    @Test
    void obtenerCalificaciones_ListaVacia_DeberiaRetornar200() throws Exception {
        when(service.obtenerCalificaciones(2L, 3L)).thenReturn(List.of());

        mockMvc.perform(get("/api/bff/calificaciones/curso/2/asignatura/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void guardarCalificaciones_DeberiaRetornar200() throws Exception {
        doNothing().when(service).guardarCalificaciones(anyList());

        String body = "[{\"usuarioUuid\":\"uuid-01\",\"asignaturaId\":1,\"nota1\":6.0,\"nota2\":5.0,\"nota3\":4.0}]";

        mockMvc.perform(put("/api/bff/calificaciones/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(service).guardarCalificaciones(anyList());
    }
}
