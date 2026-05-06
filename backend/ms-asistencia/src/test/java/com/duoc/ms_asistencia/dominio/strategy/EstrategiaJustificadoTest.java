package com.duoc.ms_asistencia.dominio.strategy;

import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EstrategiaJustificadoTest {

    @Test
    void testProcesar_ConObservacionVacia_DeberiaSetearFaltaJustificada() {
        // Given
        EstrategiaJustificado estrategia = new EstrategiaJustificado();
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), null, "");

        // When
        Asistencia result = estrategia.procesar(asistencia);

        // Then
        assertEquals("Falta justificada", result.getObservacion());
    }

    @Test
    void testProcesar_ConObservacion_DeberiaConcatenarMotivo() {
        // Given
        EstrategiaJustificado estrategia = new EstrategiaJustificado();
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), null, "Enfermedad");

        // When
        Asistencia result = estrategia.procesar(asistencia);

        // Then
        assertEquals("Falta justificada: Enfermedad", result.getObservacion());
    }

    @Test
    void testProcesar_ConObservacionNull_DeberiaSetearFaltaJustificada() {
        // Given
        EstrategiaJustificado estrategia = new EstrategiaJustificado();
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), null, null);

        // When
        Asistencia result = estrategia.procesar(asistencia);

        // Then
        assertEquals("Falta justificada", result.getObservacion());
    }
}