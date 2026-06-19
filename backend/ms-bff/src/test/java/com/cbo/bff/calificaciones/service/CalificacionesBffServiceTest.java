package com.cbo.bff.calificaciones.service;

import com.cbo.bff.calificaciones.dto.CalificacionBffResponseDTO;
import com.cbo.bff.calificaciones.dto.GuardarCalificacionBffRequestDTO;
import com.cbo.bff.calificaciones.dto.ms.CalificacionMsResponseDTO;
import com.cbo.bff.gestionacademica.client.AcademicoFeignClient;
import com.cbo.bff.gestionacademica.client.UsuarioFeignClient;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioNombreMsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalificacionesBffServiceTest {

    @Mock private AcademicoFeignClient academicoFeignClient;
    @Mock private UsuarioFeignClient usuarioFeignClient;

    @InjectMocks
    private CalificacionesBffService service;

    @Test
    void obtenerCalificaciones_DeberiaEnriquecerConNombreDeEstudiante() {
        CalificacionMsResponseDTO ms = new CalificacionMsResponseDTO(
                "uuid-est-01", 6.0, 5.5, 4.0, 5.2);

        when(academicoFeignClient.obtenerCalificacionesPorCursoYAsignatura(1L, 2L))
                .thenReturn(List.of(ms));
        when(usuarioFeignClient.obtenerNombre("uuid-est-01"))
                .thenReturn(new UsuarioNombreMsDTO("Sofía Morales"));

        List<CalificacionBffResponseDTO> resultado = service.obtenerCalificaciones(1L, 2L);

        assertEquals(1, resultado.size());
        assertEquals("Sofía Morales", resultado.get(0).getNombre());
        assertEquals(6.0, resultado.get(0).getNota1());
        assertEquals(5.2, resultado.get(0).getPromedio());
    }

    @Test
    void obtenerCalificaciones_CuandoFeignNombreFalla_DeberiaUsarUuidComoNombre() {
        CalificacionMsResponseDTO ms = new CalificacionMsResponseDTO(
                "uuid-est-02", 4.0, 3.5, 5.0, 4.2);

        when(academicoFeignClient.obtenerCalificacionesPorCursoYAsignatura(1L, 3L))
                .thenReturn(List.of(ms));
        when(usuarioFeignClient.obtenerNombre("uuid-est-02"))
                .thenThrow(new RuntimeException("MS caído"));

        List<CalificacionBffResponseDTO> resultado = service.obtenerCalificaciones(1L, 3L);

        assertEquals("uuid-est-02", resultado.get(0).getNombre());
    }

    @Test
    void obtenerCalificaciones_UsaCacheParaNoLlamarRepetidamente() {
        CalificacionMsResponseDTO ms1 = new CalificacionMsResponseDTO("uuid-est-03", 5.0, 5.0, 5.0, 5.0);
        CalificacionMsResponseDTO ms2 = new CalificacionMsResponseDTO("uuid-est-03", 6.0, 6.0, 6.0, 6.0);

        when(academicoFeignClient.obtenerCalificacionesPorCursoYAsignatura(2L, 1L))
                .thenReturn(List.of(ms1, ms2));
        when(usuarioFeignClient.obtenerNombre("uuid-est-03"))
                .thenReturn(new UsuarioNombreMsDTO("Nombre Único"));

        service.obtenerCalificaciones(2L, 1L);

        verify(usuarioFeignClient, times(1)).obtenerNombre("uuid-est-03");
    }

    @Test
    void guardarCalificaciones_DeberiaLlamarFeignPorCadaCalificacion() {
        GuardarCalificacionBffRequestDTO c1 = new GuardarCalificacionBffRequestDTO();
        c1.setUsuarioUuid("uuid-a");
        c1.setAsignaturaId(1L);
        c1.setNota1(6.0);
        c1.setNota2(5.0);
        c1.setNota3(4.0);

        GuardarCalificacionBffRequestDTO c2 = new GuardarCalificacionBffRequestDTO();
        c2.setUsuarioUuid("uuid-b");
        c2.setAsignaturaId(1L);
        c2.setNota1(5.0);
        c2.setNota2(5.0);
        c2.setNota3(5.0);

        service.guardarCalificaciones(List.of(c1, c2));

        verify(academicoFeignClient, times(2)).guardarCalificacion(any());
    }

    @Test
    void guardarCalificaciones_ListaVacia_NoDeberiaLlamarFeign() {
        service.guardarCalificaciones(List.of());

        verify(academicoFeignClient, never()).guardarCalificacion(any());
    }
}
