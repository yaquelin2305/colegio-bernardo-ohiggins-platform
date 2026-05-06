package com.duoc.ms_asistencia.dominio.strategy;

import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EstrategiaPresenteTest {

    @Test
    void testProcesar_DeberiaSetearObservacionAsistenciaRegistrada() {
        // Given
        EstrategiaPresente estrategia = new EstrategiaPresente();
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), null, "");

        // When
        Asistencia result = estrategia.procesar(asistencia);

        // Then
        assertEquals("Asistencia registrada", result.getObservacion());
    }
}