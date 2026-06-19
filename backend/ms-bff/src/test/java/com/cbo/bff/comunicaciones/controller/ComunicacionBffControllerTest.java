package com.cbo.bff.comunicaciones.controller;

import com.cbo.bff.comunicaciones.dto.DestinatarioDTO;
import com.cbo.bff.comunicaciones.dto.MensajeBffDTO;
import com.cbo.bff.comunicaciones.service.ComunicacionBffService;
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

@WebMvcTest(controllers = ComunicacionBffController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class ComunicacionBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComunicacionBffService service;

    private MensajeBffDTO mensajeMock(Long id, String asunto) {
        return MensajeBffDTO.builder()
                .id(id).remitente("Prof. García").asunto(asunto)
                .cuerpo("Contenido").canal("EMAIL").leido(false).build();
    }

    @Test
    void getBandeja_DeberiaRetornar200ConMensajes() throws Exception {
        when(service.getBandeja("uuid-01")).thenReturn(List.of(mensajeMock(1L, "Reunión")));

        mockMvc.perform(get("/api/bff/comunicaciones/bandeja/uuid-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].asunto").value("Reunión"))
                .andExpect(jsonPath("$[0].remitente").value("Prof. García"));
    }

    @Test
    void getMensaje_DeberiaRetornar200ConMensaje() throws Exception {
        when(service.getMensaje(5L)).thenReturn(mensajeMock(5L, "Citación"));

        mockMvc.perform(get("/api/bff/comunicaciones/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asunto").value("Citación"));
    }

    @Test
    void enviarMensaje_DeberiaRetornar200ConMensajeEnviado() throws Exception {
        when(service.enviarMensaje(any(), eq("uuid-rem"))).thenReturn(mensajeMock(6L, "Aviso"));

        String body = "{\"destinatario\":\"uuid-dest\",\"asunto\":\"Aviso\",\"mensaje\":\"Texto\",\"canal\":\"EMAIL\",\"tipo\":\"CIRCULAR\"}";

        mockMvc.perform(post("/api/bff/comunicaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-User-Uuid", "uuid-rem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.asunto").value("Aviso"));
    }

    @Test
    void getDestinatarios_DeberiaRetornar200ConLista() throws Exception {
        DestinatarioDTO d = new DestinatarioDTO("uuid-01", "Prof. García - Docente");
        when(service.getDestinatarios("uuid-actual")).thenReturn(List.of(d));

        mockMvc.perform(get("/api/bff/comunicaciones/destinatarios")
                        .header("X-User-Uuid", "uuid-actual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Prof. García - Docente"));
    }

    @Test
    void marcarLeido_DeberiaRetornar200ConMensajeActualizado() throws Exception {
        MensajeBffDTO dto = mensajeMock(7L, "Leído");
        when(service.marcarLeido(7L)).thenReturn(dto);

        mockMvc.perform(patch("/api/bff/comunicaciones/leido/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7));
    }
}
