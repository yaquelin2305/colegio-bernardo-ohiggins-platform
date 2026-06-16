package com.duoc.ms_asistencia.domain.strategy;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsistenciaStrategiesTest {

    private final PresenteStrategy presenteStrategy = new PresenteStrategy();
    private final AusenciaStrategy ausenciaStrategy = new AusenciaStrategy();
    private final JustificadoStrategy justificadoStrategy = new JustificadoStrategy();

    @Test
    void presenteStrategy_DeberiaAplicarSoloAPresente() {
        assertTrue(presenteStrategy.aplica("PRESENTE"));
        assertTrue(presenteStrategy.aplica("presente"));
        assertFalse(presenteStrategy.aplica("AUSENTE"));
    }

    @Test
    void ausenciaStrategy_DeberiaAplicarSoloAAusente() {
        assertTrue(ausenciaStrategy.aplica("AUSENTE"));
        assertFalse(ausenciaStrategy.aplica("JUSTIFICADO"));
    }

    @Test
    void justificadoStrategy_DeberiaAplicarSoloAJustificado() {
        assertTrue(justificadoStrategy.aplica("JUSTIFICADO"));
        assertFalse(justificadoStrategy.aplica("PRESENTE"));
    }

    @Test
    void ejecucionDeEstrategias_NoDeberiaLanzarExcepciones() {
        Asistencia asistencia = Asistencia.builder().estudianteId("ALUMNO-TEST").observacion("Test").build();
        
        // Verifica que los métodos void (que solo hacen System.out.println) no fallen
        assertDoesNotThrow(() -> presenteStrategy.procesar(asistencia));
        assertDoesNotThrow(() -> ausenciaStrategy.procesar(asistencia));
        assertDoesNotThrow(() -> justificadoStrategy.procesar(asistencia));
    }
}