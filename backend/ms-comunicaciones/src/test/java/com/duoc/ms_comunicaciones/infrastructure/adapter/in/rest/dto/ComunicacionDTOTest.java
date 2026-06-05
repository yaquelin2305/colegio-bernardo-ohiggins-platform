package com.duoc.ms_comunicaciones.infrastructure.adapter.in.rest.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ComunicacionDTOTest {

    @Test
    void testComunicacionRequestDTO() {
        ComunicacionRequestDTO request = new ComunicacionRequestDTO();
        request.setUsuarioId("USR123");
        request.setDestinatario("test@test.com");
        request.setAsunto("Asunto Test");
        request.setMensaje("Mensaje Test");
        request.setTipo("AVISO");
        request.setCanal("EMAIL"); // Corregido: Ahora pasa el String "EMAIL"

        assertEquals("USR123", request.getUsuarioId());
        assertEquals("test@test.com", request.getDestinatario());
        assertEquals("Asunto Test", request.getAsunto());
        assertEquals("Mensaje Test", request.getMensaje());
        assertEquals("AVISO", request.getTipo());
        assertEquals("EMAIL", request.getCanal()); // Corregido: Valida contra String
    }

    @Test
    void testComunicacionResponseDTO() {
        LocalDateTime ahora = LocalDateTime.now();
        ComunicacionResponseDTO response = new ComunicacionResponseDTO();
        
        response.setMensajeId(1L); // Corregido: Se cambió setId por setMensajeId
        response.setUsuarioId("USR123");
        response.setDestinatario("test@test.com");
        response.setAsunto("Asunto");
        response.setMensaje("Mensaje");
        response.setTipo("AVISO");
        response.setCanal("EMAIL"); // Corregido: Ahora pasa el String "EMAIL"
        response.setFechaEnvio(ahora);
        response.setLeido(true);

        assertEquals(1L, response.getMensajeId()); // Corregido: Se cambió getId por getMensajeId
        assertEquals("USR123", response.getUsuarioId());
        assertEquals("test@test.com", response.getDestinatario());
        assertEquals("Asunto", response.getAsunto());
        assertEquals("Mensaje", response.getMensaje());
        assertEquals("AVISO", response.getTipo());
        assertEquals("EMAIL", response.getCanal()); // Corregido: Valida contra String
        assertEquals(ahora, response.getFechaEnvio());
        assertTrue(response.isLeido());
    }
}