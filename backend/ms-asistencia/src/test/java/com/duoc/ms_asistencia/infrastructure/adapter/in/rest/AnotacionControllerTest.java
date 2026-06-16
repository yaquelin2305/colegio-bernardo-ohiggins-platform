package com.duoc.ms_asistencia.infrastructure.adapter.in.rest;

import com.duoc.ms_asistencia.domain.model.Anotacion;
import com.duoc.ms_asistencia.domain.port.in.AnotacionUseCase;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AnotacionController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class AnotacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnotacionUseCase useCase;

    @Test
    void guardar_DeberiaCrearAnotacion() throws Exception {
        Anotacion anotacionMock = Anotacion.builder()
                .id(10L).estudianteId("EST-01").tipo("MAL_COMPORTAMIENTO").descripcion("Falta de respeto")
                .fecha(LocalDate.now())
                .build();

        Mockito.when(useCase.guardar(any(Anotacion.class))).thenReturn(anotacionMock);

        String body = "{\"estudianteId\":\"EST-01\",\"tipo\":\"MAL_COMPORTAMIENTO\",\"descripcion\":\"Falta de respeto\"}";

        mockMvc.perform(post("/api/asistencia/anotaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.descripcion").value("Falta de respeto"));
    }

    @Test
    void listar_DeberiaRetornarListaDeEstudiante() throws Exception {
        Anotacion anotacion = Anotacion.builder().id(11L).estudianteId("EST-01").tipo("POSITIVA").build();
        Mockito.when(useCase.listarPorEstudiante("EST-01")).thenReturn(List.of(anotacion));

        mockMvc.perform(get("/api/asistencia/anotaciones/estudiante/EST-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("POSITIVA"));
    }
}