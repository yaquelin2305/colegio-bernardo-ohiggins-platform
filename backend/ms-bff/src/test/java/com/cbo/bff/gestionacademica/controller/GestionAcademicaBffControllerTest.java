package com.cbo.bff.gestionacademica.controller;

import com.cbo.bff.gestionacademica.dto.BoletinDto;
import com.cbo.bff.gestionacademica.dto.DashboardStatsDto;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioMsDTO;
import com.cbo.bff.gestionacademica.service.GestionAcademicaBffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GestionAcademicaBffController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class GestionAcademicaBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GestionAcademicaBffService service;

    @Test
    void obtenerBoletin_DeberiaRetornar200ConBoletin() throws Exception {
        UUID id = UUID.randomUUID();
        BoletinDto boletin = BoletinDto.builder()
                .estudianteUuid(id)
                .nombreCompleto("Ana García")
                .promedioGeneral(5.5)
                .porcentajeAsistencia(90.0)
                .build();

        when(service.obtenerBoletin(id)).thenReturn(boletin);

        mockMvc.perform(get("/api/bff/boletin/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Ana García"))
                .andExpect(jsonPath("$.promedioGeneral").value(5.5));
    }

    @Test
    void obtenerStats_DeberiaRetornar200ConStats() throws Exception {
        DashboardStatsDto stats = DashboardStatsDto.builder()
                .totalEstudiantes(100L)
                .totalDocentes(10L)
                .totalCursos(5L)
                .totalAsignaturas(8L)
                .build();

        when(service.obtenerStats()).thenReturn(stats);

        mockMvc.perform(get("/api/bff/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEstudiantes").value(100))
                .andExpect(jsonPath("$.totalDocentes").value(10));
    }

    @Test
    void listarCursos_DeberiaRetornar200ConLista() throws Exception {
        when(service.listarCursos()).thenReturn(List.of(Map.of("id", 1, "nombre", "1°A")));

        mockMvc.perform(get("/api/bff/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("1°A"));
    }

    @Test
    void listarAsignaturas_DeberiaRetornar200ConLista() throws Exception {
        when(service.listarAsignaturas()).thenReturn(List.of(Map.of("id", 2, "nombre", "Física")));

        mockMvc.perform(get("/api/bff/asignaturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Física"));
    }

    @Test
    void listarUsuariosPorRol_DeberiaRetornar200ConLista() throws Exception {
        UsuarioMsDTO u = UsuarioMsDTO.builder().id("1").nombreCompleto("Prof. Ruiz").build();
        when(service.listarUsuariosPorRol("DOCENTE")).thenReturn(List.of(u));

        mockMvc.perform(get("/api/bff/usuarios/DOCENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCompleto").value("Prof. Ruiz"));
    }
}
