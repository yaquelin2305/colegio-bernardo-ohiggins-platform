package com.duoc.ms_asistencia.application.service;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import com.duoc.ms_asistencia.domain.model.ResumenAsistencia;
import com.duoc.ms_asistencia.domain.port.out.AsistenciaRepositoryPort;
import com.duoc.ms_asistencia.domain.strategy.AsistenciaStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepositoryPort repositoryPort;

    @Spy
    private List<AsistenciaStrategy> strategies = new ArrayList<>();

    @Mock
    private AsistenciaStrategy mockStrategy;

    @InjectMocks
    private AsistenciaService asistenciaService;

    @BeforeEach
    void setUp() {
        strategies.clear();
        strategies.add(mockStrategy);
    }

    @Test
    void registrarLista_DeberiaAsignarFechaYProcesarEstrategias() {
        Asistencia asistencia = Asistencia.builder()
                .estudianteId("EST-01")
                .estado("PRESENTE")
                .build();
        List<Asistencia> listaInput = List.of(asistencia);

        when(mockStrategy.aplica("PRESENTE")).thenReturn(true);
        when(repositoryPort.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Asistencia> resultado = asistenciaService.registrarLista(listaInput);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(LocalDate.now(), resultado.get(0).getFecha());
        verify(mockStrategy, times(1)).procesar(asistencia);
        verify(repositoryPort, times(1)).saveAll(listaInput);
    }

    @Test
    void obtenerResumen_DeberiaCalcularPorcentajeCorrectamente() {
        String cursoId = "CURSO-1A";
        LocalDate fecha = LocalDate.now();
        
        List<Asistencia> asistenciasFalsas = List.of(
                Asistencia.builder().estado("PRESENTE").build(),
                Asistencia.builder().estado("PRESENTE").build(),
                Asistencia.builder().estado("AUSENTE").build(),
                Asistencia.builder().estado("JUSTIFICADO").build()
        ); 

        when(repositoryPort.findByCursoIdAndFecha(cursoId, fecha)).thenReturn(asistenciasFalsas);

        ResumenAsistencia resumen = asistenciaService.obtenerResumen(cursoId, fecha);

        assertNotNull(resumen);
        assertEquals(4, resumen.getTotal());
        assertEquals(2, resumen.getTotalPresentes());
        assertEquals(1, resumen.getTotalAusentes());
        assertEquals(1, resumen.getTotalJustificados());
        assertEquals(50.0, resumen.getPorcentajeAsistencia());
    }

    @Test
    void justificarInasistencia_DeberiaCambiarEstadoYGuardar() {
        Long idAsistencia = 123L;
        String motivo = "Licencia médica";
        Asistencia asistenciaOriginal = Asistencia.builder()
                .id(idAsistencia)
                .estudianteId("EST-02")
                .estado("AUSENTE")
                .build();

        when(repositoryPort.findById(idAsistencia)).thenReturn(Optional.of(asistenciaOriginal));
        when(mockStrategy.aplica("JUSTIFICADO")).thenReturn(true);
        when(repositoryPort.save(any(Asistencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Asistencia resultado = asistenciaService.justificarInasistencia(idAsistencia, motivo);

        assertNotNull(resultado);
        assertEquals("JUSTIFICADO", resultado.getEstado());
        assertEquals(motivo, resultado.getObservacion());
        verify(mockStrategy, times(1)).procesar(resultado);
        verify(repositoryPort, times(1)).save(resultado);
    }

    @Test
    void justificarInasistencia_DeberiaLanzarExcepcion_SiNoExiste() {
        Long idInexistente = 999L;
        when(repositoryPort.findById(idInexistente)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            asistenciaService.justificarInasistencia(idInexistente, "Cualquier motivo");
        });

        assertEquals("Asistencia no encontrada con id: " + idInexistente, exception.getMessage());
        verify(repositoryPort, never()).save(any());
    }
}