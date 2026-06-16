package com.duoc.ms_asistencia.infrastructure.adapter.in.rest;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import com.duoc.ms_asistencia.domain.model.ResumenAsistencia;
import com.duoc.ms_asistencia.domain.port.in.AsistenciaUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AsistenciaController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class AsistenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AsistenciaUseCase useCase;

    @Test
    void registrar_DeberiaRetornarListaGuardada() throws Exception {
        Asistencia asistenciaMock = Asistencia.builder()
                .id(1L).estudianteId("EST-01").cursoId("CURSO-A").estado("PRESENTE").fecha(LocalDate.now())
                .build();

        Mockito.when(useCase.registrarLista(any())).thenReturn(List.of(asistenciaMock));

        String jsonRequest = "[{\"estudianteId\":\"EST-01\",\"cursoId\":\"CURSO-A\",\"estado\":\"PRESENTE\"}]";

        mockMvc.perform(post("/api/asistencia/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("PRESENTE"));
    }

    @Test
    void getPorCurso_DeberiaRetornarRegistros() throws Exception {
        Asistencia asistenciaMock = Asistencia.builder()
                .id(2L).estudianteId("EST-02").cursoId("CURSO-A").estado("AUSENTE").fecha(LocalDate.now())
                .build();

        Mockito.when(useCase.obtenerPorCursoYFecha(eq("CURSO-A"), any(LocalDate.class)))
                .thenReturn(List.of(asistenciaMock));

        mockMvc.perform(get("/api/asistencia/curso/CURSO-A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estudianteId").value("EST-02"));
    }

    @Test
    void getByEstudiante_DeberiaRetornarLista() throws Exception {
        Mockito.when(useCase.obtenerPorEstudiante("EST-01")).thenReturn(List.of());

        mockMvc.perform(get("/api/asistencia/estudiante/EST-01"))
                .andExpect(status().isOk());
    }

    @Test
    void getResumen_DeberiaRetornarDatosCalculados() throws Exception {
        ResumenAsistencia resumen = ResumenAsistencia.builder()
                .cursoId("CURSO-A").fecha(LocalDate.now()).totalPresentes(10).totalAusentes(2)
                .totalJustificados(1).total(13).porcentajeAsistencia(76.9)
                .build();

        Mockito.when(useCase.obtenerResumen(eq("CURSO-A"), any(LocalDate.class))).thenReturn(resumen);

        mockMvc.perform(get("/api/asistencia/resumen").param("cursoId", "CURSO-A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentajeAsistencia").value(76.9));
    }

    @Test
    void getInasistencias_DeberiaRetornarLista() throws Exception {
        Mockito.when(useCase.obtenerInasistencias()).thenReturn(List.of());

        mockMvc.perform(get("/api/asistencia/inasistencias"))
                .andExpect(status().isOk());
    }

    @Test
    void justificar_DeberiaActualizarAsistencia() throws Exception {
        Asistencia actualizada = Asistencia.builder()
                .id(5L).estado("JUSTIFICADO").observacion("Medico").build();

        Mockito.when(useCase.justificarInasistencia(eq(5L), eq("Medico"))).thenReturn(actualizada);

        mockMvc.perform(patch("/api/asistencia/5/justificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\":\"Medico\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("JUSTIFICADO"));
    }
}