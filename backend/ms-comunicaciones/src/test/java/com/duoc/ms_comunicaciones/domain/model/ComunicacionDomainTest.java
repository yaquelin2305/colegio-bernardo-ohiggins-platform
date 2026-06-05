package com.duoc.ms_comunicaciones.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ComunicacionDomainTest {

    @Test
    void testComunicacionBuilderAndGetters() {
        LocalDateTime ahora = LocalDateTime.now();
        
        Comunicacion dom = Comunicacion.builder()
                .id(1L)
                .usuarioId("U1")
                .destinatario("correo@test.com")
                .asunto("Hola")
                .mensaje("Mundo")
                .tipo("EMAIL")
                .canal(Canal.EMAIL)
                .fechaEnvio(ahora)
                .leido(true)
                .build();

        assertEquals(1L, dom.getId());
        assertEquals("U1", dom.getUsuarioId());
        assertEquals("correo@test.com", dom.getDestinatario());
        assertEquals("Hola", dom.getAsunto());
        assertEquals("Mundo", dom.getMensaje());
        assertEquals("EMAIL", dom.getTipo());
        assertEquals(Canal.EMAIL, dom.getCanal());
        assertEquals(ahora, dom.getFechaEnvio());
        assertTrue(dom.isLeido());
    }
}