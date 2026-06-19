package com.cbo.bff.gestionacademica.service;

import com.cbo.bff.asistencia.client.AsistenciaFeignClient;
import com.cbo.bff.asistencia.dto.ms.AsistenciaMsResponseDTO;
import com.cbo.bff.gestionacademica.client.AcademicoFeignClient;
import com.cbo.bff.gestionacademica.client.UsuarioFeignClient;
import com.cbo.bff.gestionacademica.dto.BoletinDto;
import com.cbo.bff.gestionacademica.dto.CalificacionesAcademicoDto;
import com.cbo.bff.gestionacademica.dto.DashboardStatsDto;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioMsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionAcademicaBffServiceTest {

    @Mock private AcademicoFeignClient academicoFeignClient;
    @Mock private UsuarioFeignClient usuarioFeignClient;
    @Mock private AsistenciaFeignClient asistenciaFeignClient;

    @InjectMocks
    private GestionAcademicaBffService service;

    @Test
    void obtenerBoletin_DeberiaCalcularPromedioYPorcentaje() {
        UUID estudianteId = UUID.randomUUID();

        CalificacionesAcademicoDto cal = new CalificacionesAcademicoDto(
                estudianteId, 1L, 6.0, 5.0, 4.0, 5.0);

        Map<String, Object> asignatura = Map.of("id", 1, "nombre", "Matemáticas");
        Map<String, Object> curso = Map.of("id", 1, "nombre", "1°A");
        Map<String, Object> matricula = Map.of("cursoId", 1);

        AsistenciaMsResponseDTO presente = new AsistenciaMsResponseDTO();
        presente.setEstado("PRESENTE");
        AsistenciaMsResponseDTO ausente = new AsistenciaMsResponseDTO();
        ausente.setEstado("AUSENTE");

        UsuarioMsDTO usuario = UsuarioMsDTO.builder()
                .nombreCompleto("Ana García").rut("12345678-9").build();

        when(academicoFeignClient.obtenerCalificacionesPorEstudiante(estudianteId))
                .thenReturn(List.of(cal));
        when(academicoFeignClient.listarAsignaturas()).thenReturn(List.of(asignatura));
        when(academicoFeignClient.listarCursos()).thenReturn(List.of(curso));
        when(academicoFeignClient.listarMatriculasPorEstudiante(estudianteId))
                .thenReturn(List.of(matricula));
        when(asistenciaFeignClient.getPorEstudiante(estudianteId.toString()))
                .thenReturn(List.of(presente, ausente));
        when(usuarioFeignClient.obtenerPorId(estudianteId)).thenReturn(usuario);

        BoletinDto boletin = service.obtenerBoletin(estudianteId);

        assertNotNull(boletin);
        assertEquals("Ana García", boletin.getNombreCompleto());
        assertEquals(5.0, boletin.getPromedioGeneral());
        assertEquals(50.0, boletin.getPorcentajeAsistencia());
        assertEquals("1°A", boletin.getCurso());
        assertEquals(1, boletin.getCalificaciones().size());
        assertEquals("Matemáticas", boletin.getCalificaciones().get(0).getAsignaturaNombre());
    }

    @Test
    void obtenerBoletin_SinAsistencias_PorcentajeDebeSerNull() {
        UUID estudianteId = UUID.randomUUID();

        when(academicoFeignClient.obtenerCalificacionesPorEstudiante(estudianteId))
                .thenReturn(List.of());
        when(academicoFeignClient.listarAsignaturas()).thenReturn(List.of());
        when(academicoFeignClient.listarCursos()).thenReturn(List.of());
        when(academicoFeignClient.listarMatriculasPorEstudiante(estudianteId))
                .thenReturn(List.of());
        when(asistenciaFeignClient.getPorEstudiante(estudianteId.toString()))
                .thenReturn(List.of());
        when(usuarioFeignClient.obtenerPorId(estudianteId)).thenReturn(null);

        BoletinDto boletin = service.obtenerBoletin(estudianteId);

        assertNull(boletin.getPorcentajeAsistencia());
        assertEquals(0.0, boletin.getPromedioGeneral());
        assertNull(boletin.getCurso());
    }

    @Test
    void obtenerBoletin_JustificadoContaComoPresente() {
        UUID estudianteId = UUID.randomUUID();

        AsistenciaMsResponseDTO presente = new AsistenciaMsResponseDTO();
        presente.setEstado("PRESENTE");
        AsistenciaMsResponseDTO justificado = new AsistenciaMsResponseDTO();
        justificado.setEstado("JUSTIFICADO");
        AsistenciaMsResponseDTO ausente = new AsistenciaMsResponseDTO();
        ausente.setEstado("AUSENTE");

        when(academicoFeignClient.obtenerCalificacionesPorEstudiante(estudianteId)).thenReturn(List.of());
        when(academicoFeignClient.listarAsignaturas()).thenReturn(List.of());
        when(academicoFeignClient.listarCursos()).thenReturn(List.of());
        when(academicoFeignClient.listarMatriculasPorEstudiante(estudianteId)).thenReturn(List.of());
        when(asistenciaFeignClient.getPorEstudiante(estudianteId.toString()))
                .thenReturn(List.of(presente, justificado, ausente));
        when(usuarioFeignClient.obtenerPorId(estudianteId)).thenReturn(null);

        BoletinDto boletin = service.obtenerBoletin(estudianteId);

        // 2 de 3 presentes/justificados = 66.7%
        assertEquals(66.7, boletin.getPorcentajeAsistencia());
    }

    @Test
    void obtenerStats_DeberiaAgregarConteosDe4Llamadas() {
        UsuarioMsDTO e1 = UsuarioMsDTO.builder().id("1").build();
        UsuarioMsDTO e2 = UsuarioMsDTO.builder().id("2").build();
        UsuarioMsDTO d1 = UsuarioMsDTO.builder().id("3").build();

        when(usuarioFeignClient.listarPorRol("ESTUDIANTE")).thenReturn(List.of(e1, e2));
        when(usuarioFeignClient.listarPorRol("DOCENTE")).thenReturn(List.of(d1));
        when(academicoFeignClient.listarCursos())
                .thenReturn(List.of(Map.of("id", 1), Map.of("id", 2)));
        when(academicoFeignClient.listarAsignaturas())
                .thenReturn(List.of(Map.of("id", 1), Map.of("id", 2), Map.of("id", 3)));

        DashboardStatsDto stats = service.obtenerStats();

        assertEquals(2L, stats.getTotalEstudiantes());
        assertEquals(1L, stats.getTotalDocentes());
        assertEquals(2L, stats.getTotalCursos());
        assertEquals(3L, stats.getTotalAsignaturas());
    }

    @Test
    void listarCursos_DeberiaRetornarListaDelMS() {
        List<Map<String, Object>> esperado = List.of(Map.of("id", 1, "nombre", "1°A"));
        when(academicoFeignClient.listarCursos()).thenReturn(esperado);

        List<Map<String, Object>> resultado = service.listarCursos();

        assertEquals(1, resultado.size());
        verify(academicoFeignClient, times(1)).listarCursos();
    }

    @Test
    void listarAsignaturas_DeberiaRetornarListaDelMS() {
        List<Map<String, Object>> esperado = List.of(Map.of("id", 1, "nombre", "Física"));
        when(academicoFeignClient.listarAsignaturas()).thenReturn(esperado);

        List<Map<String, Object>> resultado = service.listarAsignaturas();

        assertEquals(1, resultado.size());
        verify(academicoFeignClient, times(1)).listarAsignaturas();
    }

    @Test
    void listarUsuariosPorRol_DeberiaRetornarListaDelMS() {
        UsuarioMsDTO usuario = UsuarioMsDTO.builder().id("1").nombreCompleto("Juan").build();
        when(usuarioFeignClient.listarPorRol("DOCENTE")).thenReturn(List.of(usuario));

        List<UsuarioMsDTO> resultado = service.listarUsuariosPorRol("DOCENTE");

        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombreCompleto());
    }
}
