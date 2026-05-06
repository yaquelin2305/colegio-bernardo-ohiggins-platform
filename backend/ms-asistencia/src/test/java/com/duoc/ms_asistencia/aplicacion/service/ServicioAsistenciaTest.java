package com.duoc.ms_asistencia.aplicacion.service;

import com.duoc.ms_asistencia.aplicacion.port.PuertoRepositorioAsistencia;
import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import com.duoc.ms_asistencia.dominio.entity.EstadoAsistencia;
import com.duoc.ms_asistencia.dominio.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioAsistenciaTest {

    private PuertoRepositorioAsistencia repositorio;
    private Map<EstadoAsistencia, EstrategiaAsistencia> estrategias;
    private ServicioAsistencia servicio;

    @BeforeEach
    void setUp() {
        repositorio = mock(PuertoRepositorioAsistencia.class);
        estrategias = new HashMap<>();
        // Create real strategy instances
        EstrategiaPresente presente = new EstrategiaPresente();
        EstrategiaAusente ausente = new EstrategiaAusente();
        EstrategiaJustificado justificado = new EstrategiaJustificado();
        estrategias.put(EstadoAsistencia.PRESENTE, presente);
        estrategias.put(EstadoAsistencia.AUSENTE, ausente);
        estrategias.put(EstadoAsistencia.JUSTIFICADO, justificado);
        servicio = new ServicioAsistencia(repositorio, estrategias);
    }

    @Test
    void testRegistrarAsistencia_Presente() {
        // Given
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), EstadoAsistencia.PRESENTE, "");
        when(repositorio.guardar(any())).thenReturn(asistencia);

        // When
        Asistencia result = servicio.registrarAsistencia(asistencia);

        // Then
        assertNotNull(result);
        verify(repositorio).guardar(asistencia);
        assertEquals("Asistencia registrada", result.getObservacion());
    }

    @Test
    void testRegistrarAsistencia_Ausente() {
        // Given
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), EstadoAsistencia.AUSENTE, "");
        when(repositorio.guardar(any())).thenReturn(asistencia);

        // When
        Asistencia result = servicio.registrarAsistencia(asistencia);

        // Then
        assertNotNull(result);
        verify(repositorio).guardar(asistencia);
        assertEquals("Falta registrada sin justificar", result.getObservacion());
    }

    @Test
    void testRegistrarAsistencia_Justificado() {
        // Given
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), EstadoAsistencia.JUSTIFICADO, "Enfermedad");
        when(repositorio.guardar(any())).thenReturn(asistencia);

        // When
        Asistencia result = servicio.registrarAsistencia(asistencia);

        // Then
        assertNotNull(result);
        verify(repositorio).guardar(asistencia);
        assertTrue(result.getObservacion().contains("Falta justificada"));
    }

    @Test
    void testBuscarPorId_Existente() {
        // Given
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), EstadoAsistencia.PRESENTE, "");
        asistencia.setId(1L);
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(asistencia));

        // When
        Asistencia result = servicio.buscarPorId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testBuscarPorId_Inexistente() {
        // Given
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        // When
        Asistencia result = servicio.buscarPorId(99L);

        // Then
        assertNull(result);
    }
}