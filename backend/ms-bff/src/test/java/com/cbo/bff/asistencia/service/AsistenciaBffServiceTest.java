package com.cbo.bff.asistencia.service;

import com.cbo.bff.asistencia.client.AsistenciaFeignClient;
import com.cbo.bff.asistencia.dto.*;
import com.cbo.bff.asistencia.dto.ms.*;
import com.cbo.bff.gestionacademica.client.AcademicoFeignClient;
import com.cbo.bff.gestionacademica.client.UsuarioFeignClient;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioMsDTO;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioNombreMsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaBffServiceTest {

    @Mock private AsistenciaFeignClient asistenciaFeignClient;
    @Mock private UsuarioFeignClient usuarioFeignClient;
    @Mock private AcademicoFeignClient academicoFeignClient;

    @InjectMocks
    private AsistenciaBffService service;

    private AsistenciaMsResponseDTO crearMsResponse(Long id, String estudianteId, String cursoId, String estado) {
        AsistenciaMsResponseDTO ms = new AsistenciaMsResponseDTO();
        ms.setId(id);
        ms.setEstudianteId(estudianteId);
        ms.setCursoId(cursoId);
        ms.setEstado(estado);
        ms.setFecha(LocalDate.now());
        return ms;
    }

    @Test
    void registrar_DeberiaEnriquecerConNombreDeEstudiante() {
        AsistenciaRequestBffDTO req = new AsistenciaRequestBffDTO();
        req.setEstudianteId("uuid-01");
        req.setCursoId("C1");
        req.setEstado("PRESENTE");

        AsistenciaMsResponseDTO msResp = crearMsResponse(1L, "uuid-01", "C1", "PRESENTE");

        when(asistenciaFeignClient.registrar(anyList())).thenReturn(List.of(msResp));
        when(usuarioFeignClient.obtenerNombre("uuid-01"))
                .thenReturn(new UsuarioNombreMsDTO("María López"));

        List<EstudianteAsistenciaBffDTO> resultado = service.registrar(List.of(req));

        assertEquals(1, resultado.size());
        assertEquals("María López", resultado.get(0).getNombre());
        assertEquals("presente", resultado.get(0).getEstado());
    }

    @Test
    void registrar_CuandoFeignNombreFalla_DeberiaUsarUuidComoNombre() {
        AsistenciaRequestBffDTO req = new AsistenciaRequestBffDTO();
        req.setEstudianteId("uuid-02");
        req.setCursoId("C1");
        req.setEstado("AUSENTE");

        AsistenciaMsResponseDTO msResp = crearMsResponse(2L, "uuid-02", "C1", "AUSENTE");

        when(asistenciaFeignClient.registrar(anyList())).thenReturn(List.of(msResp));
        when(usuarioFeignClient.obtenerNombre("uuid-02")).thenThrow(new RuntimeException("MS caído"));

        List<EstudianteAsistenciaBffDTO> resultado = service.registrar(List.of(req));

        assertEquals("uuid-02", resultado.get(0).getNombre());
    }

    @Test
    void getPorCurso_DeberiaRetornarListaEnriquecida() {
        AsistenciaMsResponseDTO msResp = crearMsResponse(3L, "uuid-03", "C2", "PRESENTE");

        when(asistenciaFeignClient.getPorCurso("C2", LocalDate.now()))
                .thenReturn(List.of(msResp));
        when(usuarioFeignClient.obtenerNombre("uuid-03"))
                .thenReturn(new UsuarioNombreMsDTO("Carlos Ruiz"));

        List<EstudianteAsistenciaBffDTO> resultado = service.getPorCurso("C2", LocalDate.now());

        assertEquals(1, resultado.size());
        assertEquals("Carlos Ruiz", resultado.get(0).getNombre());
    }

    @Test
    void getPorEstudiante_DeberiaRetornarHistorialConNombre() {
        AsistenciaMsResponseDTO msResp = crearMsResponse(4L, "uuid-04", "C3", "AUSENTE");
        msResp.setObservacion("Fiebre");

        when(usuarioFeignClient.obtenerNombre("uuid-04"))
                .thenReturn(new UsuarioNombreMsDTO("Luisa Pérez"));
        when(asistenciaFeignClient.getPorEstudiante("uuid-04")).thenReturn(List.of(msResp));

        List<HistorialAsistenciaBffDTO> resultado = service.getPorEstudiante("uuid-04");

        assertEquals(1, resultado.size());
        assertEquals("ausente", resultado.get(0).getEstado());
        assertEquals("Fiebre", resultado.get(0).getAnotacion());
    }

    @Test
    void getResumen_DeberiaEnriquecerConNombreDeCurso() {
        ResumenAsistenciaMsDTO ms = new ResumenAsistenciaMsDTO();
        ms.setTotal(10);
        ms.setTotalPresentes(8);
        ms.setTotalAusentes(1);
        ms.setTotalJustificados(1);
        ms.setPorcentajeAsistencia(90.0);

        when(asistenciaFeignClient.getResumen(eq("1"), any())).thenReturn(ms);
        when(academicoFeignClient.listarCursos())
                .thenReturn(List.of(Map.of("id", 1, "nombre", "1°A")));

        ResumenAsistenciaBffDTO resultado = service.getResumen("1", null);

        assertEquals(10, resultado.getTotal());
        assertEquals(90.0, resultado.getPorcentaje());
        assertEquals("1°A", resultado.getNombreCurso());
    }

    @Test
    void getInasistencias_DeberiaRetornarListaConNombreYCurso() {
        AsistenciaMsResponseDTO ms = crearMsResponse(5L, "uuid-05", "1", "AUSENTE");

        when(asistenciaFeignClient.getInasistencias()).thenReturn(List.of(ms));
        when(usuarioFeignClient.obtenerNombre("uuid-05"))
                .thenReturn(new UsuarioNombreMsDTO("Pedro Soto"));
        when(academicoFeignClient.listarCursos())
                .thenReturn(List.of(Map.of("id", 1, "nombre", "2°B")));

        List<InasistenciaBffDTO> resultado = service.getInasistencias();

        assertEquals(1, resultado.size());
        assertEquals("Pedro Soto", resultado.get(0).getAlumno());
        assertEquals("2°B", resultado.get(0).getCurso());
        assertFalse(resultado.get(0).isJustificada());
    }

    @Test
    void getInasistencias_Justificada_DeberiaMarcarseComoJustificada() {
        AsistenciaMsResponseDTO ms = crearMsResponse(6L, "uuid-06", "1", "JUSTIFICADO");

        when(asistenciaFeignClient.getInasistencias()).thenReturn(List.of(ms));
        when(usuarioFeignClient.obtenerNombre("uuid-06")).thenReturn(new UsuarioNombreMsDTO("Rosa Díaz"));
        when(academicoFeignClient.listarCursos()).thenReturn(List.of());

        List<InasistenciaBffDTO> resultado = service.getInasistencias();

        assertTrue(resultado.get(0).isJustificada());
    }

    @Test
    void justificar_DeberiaActualizarEstadoCorrectamente() {
        AsistenciaMsResponseDTO ms = crearMsResponse(7L, "uuid-07", "C1", "JUSTIFICADO");
        JustificacionBffRequestDTO req = new JustificacionBffRequestDTO();
        req.setMotivo("Médico");

        when(asistenciaFeignClient.justificar(eq(7L), any())).thenReturn(ms);
        when(usuarioFeignClient.obtenerNombre("uuid-07")).thenReturn(new UsuarioNombreMsDTO("Tomás Vera"));
        when(academicoFeignClient.listarCursos()).thenReturn(List.of());

        InasistenciaBffDTO resultado = service.justificar(7L, req);

        assertTrue(resultado.isJustificada());
        verify(asistenciaFeignClient).justificar(eq(7L), any());
    }

    @Test
    void getAlumnosPorCurso_DeberiaRetornarAlumnosConNombre() {
        String uuid = UUID.randomUUID().toString();
        Map<String, Object> matricula = Map.of("usuarioUuid", uuid);
        UsuarioMsDTO usuario = UsuarioMsDTO.builder()
                .id(uuid).nombreCompleto("Camila Torres").rut("11111111-1").build();

        when(academicoFeignClient.obtenerEstudiantesPorCurso(1L)).thenReturn(List.of(matricula));
        when(usuarioFeignClient.obtenerPorId(any())).thenReturn(usuario);

        List<AlumnoBffDTO> resultado = service.getAlumnosPorCurso(1L);

        assertEquals(1, resultado.size());
        assertEquals("Camila Torres", resultado.get(0).getNombre());
        assertEquals("11111111-1", resultado.get(0).getRut());
    }

    @Test
    void getAlumnosPorCurso_CuandoUsuarioFeignFalla_DeberiaUsarUuidComoNombre() {
        String uuid = UUID.randomUUID().toString();
        Map<String, Object> matricula = Map.of("usuarioUuid", uuid);

        when(academicoFeignClient.obtenerEstudiantesPorCurso(2L)).thenReturn(List.of(matricula));
        when(usuarioFeignClient.obtenerPorId(any())).thenThrow(new RuntimeException("MS caído"));

        List<AlumnoBffDTO> resultado = service.getAlumnosPorCurso(2L);

        assertEquals(uuid, resultado.get(0).getNombre());
    }
}
