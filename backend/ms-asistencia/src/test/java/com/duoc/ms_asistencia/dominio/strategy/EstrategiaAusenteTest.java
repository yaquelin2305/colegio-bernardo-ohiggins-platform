package com.duoc.ms_asistencia.dominio.strategy;

import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EstrategiaAusenteTest {

    @Test
    void testProcesar_DeberiaSetearObservacionFaltaSinJustificar() {
        // Given
        EstrategiaAusente estrategia = new EstrategiaAusente();
        Asistencia asistencia = new Asistencia(1L, LocalDate.now(), null, "");

        // When
        Asistencia result = estrategia.procesar(asistencia);

        // Then
        assertEquals("Falta registrada sin justificar", result.getObservacion());
    }
}