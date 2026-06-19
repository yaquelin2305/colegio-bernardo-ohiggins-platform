package com.cbo.bff.asistencia.controller;

import com.cbo.bff.asistencia.dto.*;
import com.cbo.bff.asistencia.service.AsistenciaBffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AsistenciaBffController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class AsistenciaBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AsistenciaBffService service;

    @Test
    void registrar_DeberiaRetornar200ConLista() throws Exception {
        EstudianteAsistenciaBffDTO dto = EstudianteAsistenciaBffDTO.builder()
                .id(1L).nombre("Ana").estado("presente").build();
        when(service.registrar(anyList())).thenReturn(List.of(dto));

        String body = "[{\"estudianteId\":\"uuid-01\",\"cursoId\":\"C1\",\"estado\":\"PRESENTE\"}]";

        mockMvc.perform(post("/api/bff/asistencia/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].estado").value("presente"));
    }

    @Test
    void getPorCurso_DeberiaRetornar200ConLista() throws Exception {
        EstudianteAsistenciaBffDTO dto = EstudianteAsistenciaBffDTO.builder()
                .id(2L).nombre("Luis").estado("ausente").build();
        when(service.getPorCurso(eq("C1"), any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/bff/asistencia/curso/C1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Luis"));
    }

    @Test
    void getPorEstudiante_DeberiaRetornar200ConHistorial() throws Exception {
        HistorialAsistenciaBffDTO dto = HistorialAsistenciaBffDTO.builder()
                .id(3L).estado("presente").fecha(LocalDate.now()).build();
        when(service.getPorEstudiante("uuid-01")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/bff/asistencia/estudiante/uuid-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("presente"));
    }

    @Test
    void getResumen_DeberiaRetornar200ConResumen() throws Exception {
        ResumenAsistenciaBffDTO dto = ResumenAsistenciaBffDTO.builder()
                .total(10).presentes(8).ausentes(2).porcentaje(80.0).build();
        when(service.getResumen(eq("C1"), any())).thenReturn(dto);

        mockMvc.perform(get("/api/bff/asistencia/resumen").param("cursoId", "C1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentaje").value(80.0));
    }

    @Test
    void getInasistencias_DeberiaRetornar200ConLista() throws Exception {
        InasistenciaBffDTO dto = InasistenciaBffDTO.builder()
                .id(5L).alumno("Pedro").justificada(false).build();
        when(service.getInasistencias()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/bff/asistencia/inasistencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].alumno").value("Pedro"));
    }

    @Test
    void justificar_DeberiaRetornar200ConInasistenciaActualizada() throws Exception {
        InasistenciaBffDTO dto = InasistenciaBffDTO.builder()
                .id(5L).alumno("Pedro").justificada(true).build();
        when(service.justificar(eq(5L), any())).thenReturn(dto);

        mockMvc.perform(patch("/api/bff/asistencia/5/justificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\":\"Médico\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.justificada").value(true));
    }

    @Test
    void guardarAnotacion_DeberiaRetornar201() throws Exception {
        AnotacionBffDTO dto = AnotacionBffDTO.builder()
                .id(1L).estudianteId("uuid-01").tipo("POSITIVA").build();
        when(service.guardarAnotacion(any())).thenReturn(dto);

        mockMvc.perform(post("/api/bff/asistencia/anotaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":\"uuid-01\",\"tipo\":\"POSITIVA\",\"descripcion\":\"Buen trabajo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("POSITIVA"));
    }

    @Test
    void getAnotaciones_DeberiaRetornar200ConLista() throws Exception {
        AnotacionBffDTO dto = AnotacionBffDTO.builder().id(2L).tipo("NEGATIVA").build();
        when(service.getAnotacionesPorEstudiante("uuid-01")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/bff/asistencia/anotaciones/estudiante/uuid-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("NEGATIVA"));
    }

    @Test
    void getAlumnosPorCurso_DeberiaRetornar200ConAlumnos() throws Exception {
        AlumnoBffDTO dto = AlumnoBffDTO.builder().estudianteId("uuid-01").nombre("Sofía").build();
        when(service.getAlumnosPorCurso(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/bff/asistencia/alumnos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Sofía"));
    }
}
