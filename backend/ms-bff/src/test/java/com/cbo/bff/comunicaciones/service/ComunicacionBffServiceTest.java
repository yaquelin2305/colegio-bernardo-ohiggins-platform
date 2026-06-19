package com.cbo.bff.comunicaciones.service;

import com.cbo.bff.comunicaciones.client.ComunicacionFeignClient;
import com.cbo.bff.comunicaciones.dto.DestinatarioDTO;
import com.cbo.bff.comunicaciones.dto.EnviarMensajeRequestDTO;
import com.cbo.bff.comunicaciones.dto.MensajeBffDTO;
import com.cbo.bff.comunicaciones.dto.ms.ComunicacionMsResponseDTO;
import com.cbo.bff.gestionacademica.client.UsuarioFeignClient;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioMsDTO;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioNombreMsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacionBffServiceTest {

    @Mock private ComunicacionFeignClient comunicacionFeignClient;
    @Mock private UsuarioFeignClient usuarioFeignClient;

    @InjectMocks
    private ComunicacionBffService service;

    private ComunicacionMsResponseDTO crearMsResponse(Long id, String usuarioId, String asunto) {
        ComunicacionMsResponseDTO ms = new ComunicacionMsResponseDTO();
        ms.setMensajeId(id);
        ms.setUsuarioId(usuarioId);
        ms.setAsunto(asunto);
        ms.setMensaje("Contenido del mensaje");
        ms.setCanal("EMAIL");
        ms.setTipo("CIRCULAR");
        ms.setFechaEnvio("2026-06-18");
        ms.setLeido(false);
        return ms;
    }

    @Test
    void getBandeja_DeberiaResolverNombreRemitente() {
        ComunicacionMsResponseDTO ms = crearMsResponse(1L, "uuid-rem-01", "Reunión");

        when(comunicacionFeignClient.getBandeja("uuid-dest-01")).thenReturn(List.of(ms));
        when(usuarioFeignClient.obtenerNombre("uuid-rem-01"))
                .thenReturn(new UsuarioNombreMsDTO("Prof. García"));

        List<MensajeBffDTO> resultado = service.getBandeja("uuid-dest-01");

        assertEquals(1, resultado.size());
        assertEquals("Prof. García", resultado.get(0).getRemitente());
        assertEquals("Reunión", resultado.get(0).getAsunto());
    }

    @Test
    void getBandeja_CuandoFeignNombreFalla_DeberiaUsarUuidComoRemitente() {
        ComunicacionMsResponseDTO ms = crearMsResponse(2L, "uuid-rem-02", "Aviso");

        when(comunicacionFeignClient.getBandeja("uuid-dest-02")).thenReturn(List.of(ms));
        when(usuarioFeignClient.obtenerNombre("uuid-rem-02"))
                .thenThrow(new RuntimeException("MS caído"));

        List<MensajeBffDTO> resultado = service.getBandeja("uuid-dest-02");

        assertEquals("uuid-rem-02", resultado.get(0).getRemitente());
    }

    @Test
    void getMensaje_DeberiaRetornarMensajeEnriquecido() {
        ComunicacionMsResponseDTO ms = crearMsResponse(3L, "uuid-rem-03", "Citación");

        when(comunicacionFeignClient.getMensaje(3L)).thenReturn(ms);
        when(usuarioFeignClient.obtenerNombre("uuid-rem-03"))
                .thenReturn(new UsuarioNombreMsDTO("Directora Muñoz"));

        MensajeBffDTO resultado = service.getMensaje(3L);

        assertNotNull(resultado);
        assertEquals("Directora Muñoz", resultado.getRemitente());
        assertEquals("Citación", resultado.getAsunto());
    }

    @Test
    void getMensaje_RemitenteEsSistema_DeberiaRetornarSistema() {
        ComunicacionMsResponseDTO ms = crearMsResponse(4L,
                "00000000-0000-0000-0000-000000000000", "Notificación automática");

        when(comunicacionFeignClient.getMensaje(4L)).thenReturn(ms);

        MensajeBffDTO resultado = service.getMensaje(4L);

        assertEquals("Sistema", resultado.getRemitente());
        verify(usuarioFeignClient, never()).obtenerNombre(any());
    }

    @Test
    void enviarMensaje_DeberiaResolverNombreRemitenteYRetornarMensaje() {
        EnviarMensajeRequestDTO req = new EnviarMensajeRequestDTO();
        req.setDestinatario("uuid-dest");
        req.setAsunto("Tarea pendiente");
        req.setMensaje("Por favor completar");
        req.setCanal("EMAIL");
        req.setTipo("CIRCULAR");

        ComunicacionMsResponseDTO msResp = crearMsResponse(5L, "uuid-rem-05", "Tarea pendiente");

        when(comunicacionFeignClient.enviar(any())).thenReturn(msResp);
        when(usuarioFeignClient.obtenerNombre("uuid-rem-rte"))
                .thenReturn(new UsuarioNombreMsDTO("Juan Maestro"));
        when(usuarioFeignClient.obtenerNombre("uuid-rem-05"))
                .thenReturn(new UsuarioNombreMsDTO("Juan Maestro"));

        MensajeBffDTO resultado = service.enviarMensaje(req, "uuid-rem-rte");

        assertNotNull(resultado);
        assertEquals("Juan Maestro", resultado.getRemitente());
    }

    @Test
    void getDestinatarios_DeberiaAgregarTresRoles() {
        UsuarioMsDTO apoderado = UsuarioMsDTO.builder()
                .id("uuid-ap-01").nombreCompleto("María Silva").build();
        UsuarioMsDTO docente = UsuarioMsDTO.builder()
                .id("uuid-doc-01").nombreCompleto("Prof. Ramírez").build();
        UsuarioMsDTO admin = UsuarioMsDTO.builder()
                .id("uuid-adm-01").nombreCompleto("Admin Rivas").build();

        when(usuarioFeignClient.listarPorRol("APODERADO")).thenReturn(List.of(apoderado));
        when(usuarioFeignClient.listarPorRol("DOCENTE")).thenReturn(List.of(docente));
        when(usuarioFeignClient.listarPorRol("ADMIN")).thenReturn(List.of(admin));

        List<DestinatarioDTO> resultado = service.getDestinatarios("uuid-otro");

        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().anyMatch(d -> d.getNombre().contains("Apoderado")));
        assertTrue(resultado.stream().anyMatch(d -> d.getNombre().contains("Docente")));
        assertTrue(resultado.stream().anyMatch(d -> d.getNombre().contains("Administrador")));
    }

    @Test
    void getDestinatarios_DeberiaExcluirAlUsuarioActual() {
        UsuarioMsDTO yo = UsuarioMsDTO.builder()
                .id("uuid-yo").nombreCompleto("Yo mismo").build();
        UsuarioMsDTO otro = UsuarioMsDTO.builder()
                .id("uuid-otro").nombreCompleto("Otro Docente").build();

        when(usuarioFeignClient.listarPorRol("APODERADO")).thenReturn(List.of());
        when(usuarioFeignClient.listarPorRol("DOCENTE")).thenReturn(List.of(yo, otro));
        when(usuarioFeignClient.listarPorRol("ADMIN")).thenReturn(List.of());

        List<DestinatarioDTO> resultado = service.getDestinatarios("uuid-yo");

        assertEquals(1, resultado.size());
        assertEquals("uuid-otro", resultado.get(0).getId());
    }

    @Test
    void marcarLeido_DeberiaRetornarMensajeActualizado() {
        ComunicacionMsResponseDTO ms = crearMsResponse(6L, "uuid-rem-06", "Leído");
        ms.setLeido(true);

        when(comunicacionFeignClient.marcarLeido(6L)).thenReturn(ms);
        when(usuarioFeignClient.obtenerNombre("uuid-rem-06"))
                .thenReturn(new UsuarioNombreMsDTO("Remitente X"));

        MensajeBffDTO resultado = service.marcarLeido(6L);

        assertTrue(resultado.isLeido());
        verify(comunicacionFeignClient).marcarLeido(6L);
    }
}
