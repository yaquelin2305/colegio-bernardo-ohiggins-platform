package com.duoc.ms_comunicaciones.application.service.strategy;

import com.duoc.ms_comunicaciones.domain.model.Canal;
import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstrategiasComunicacionesTest {

    private final EmailStrategy emailStrategy = new EmailStrategy();
    private final SmsStrategy smsStrategy = new SmsStrategy();
    private final WhatsappStrategy whatsappStrategy = new WhatsappStrategy();

    @Test
    void testEmailStrategy_SoportesYDespacho() {
        Comunicacion com = Comunicacion.builder()
                .canal(Canal.EMAIL)
                .destinatario("correo@test.com")
                .asunto("Hola")
                .build();

        assertTrue(emailStrategy.supports(Canal.EMAIL));
        assertFalse(emailStrategy.supports(Canal.SMS));
        assertDoesNotThrow(() -> emailStrategy.dispatch(com));
    }

    @Test
    void testSmsStrategy_SoportesYDespacho() {
        Comunicacion com = Comunicacion.builder()
                .canal(Canal.SMS)
                .destinatario("+56912345678")
                .mensaje("Texto SMS")
                .build();

        assertTrue(smsStrategy.supports(Canal.SMS));
        assertFalse(smsStrategy.supports(Canal.EMAIL));
        assertDoesNotThrow(() -> smsStrategy.dispatch(com));
    }

    @Test
    void testWhatsappStrategy_SoportesYDespacho() {
        Comunicacion com = Comunicacion.builder()
                .canal(Canal.WHATSAPP)
                .destinatario("56987654321")
                .asunto("Alerta")
                .mensaje("Mensaje con espacios")
                .build();

        assertTrue(whatsappStrategy.supports(Canal.WHATSAPP));
        assertFalse(whatsappStrategy.supports(Canal.EMAIL));
        
        // Verifica que la codificación URL del componente no rompa el flujo
        assertDoesNotThrow(() -> whatsappStrategy.dispatch(com));
    }
}