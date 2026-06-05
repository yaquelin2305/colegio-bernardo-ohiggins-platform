package com.duoc.ms_comunicaciones.application.service.strategy;

import com.duoc.ms_comunicaciones.domain.model.Canal;
import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.domain.port.in.ComunicacionStrategy;
import com.duoc.ms_comunicaciones.domain.port.out.ComunicacionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacionServiceTest {

    @Mock
    private ComunicacionRepositoryPort repositoryPort;

    @Mock
    private ComunicacionStrategy emailStrategy;

    @Mock
    private ComunicacionStrategy smsStrategy;

    private ComunicacionService comunicacionService;

    @BeforeEach
    void setUp() {
        // Inicializamos el servicio con las estrategias mockeadas
        List<ComunicacionStrategy> strategies = Arrays.asList(emailStrategy, smsStrategy);
        comunicacionService = new ComunicacionService(repositoryPort, strategies);
    }

    @Test
    void enviar_DeberiaDespacharYGuardar_CuandoElCanalEsSoportado() {
        // Arrange
        Comunicacion comunicacion = Comunicacion.builder()
                .canal(Canal.EMAIL)
                .destinatario("test@duoc.cl")
                .asunto("Aviso")
                .mensaje("Hola Mundo")
                .build();

        when(emailStrategy.supports(Canal.EMAIL)).thenReturn(true);
        when(repositoryPort.save(comunicacion)).thenReturn(comunicacion);

        // Act
        Comunicacion resultado = comunicacionService.enviar(comunicacion);

        // Assert
        assertNotNull(resultado);
        verify(emailStrategy, times(1)).dispatch(comunicacion);
        verify(repositoryPort, times(1)).save(comunicacion);
    }

    @Test
    void enviar_DeberiaLanzarExcepcion_CuandoElCanalNoEsSoportado() {
        // Arrange
        Comunicacion comunicacion = Comunicacion.builder()
                .canal(Canal.WHATSAPP)
                .build();

        // Ambas estrategias mockeadas dirán que no soportan WHATSAPP
        when(emailStrategy.supports(Canal.WHATSAPP)).thenReturn(false);
        when(smsStrategy.supports(Canal.WHATSAPP)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            comunicacionService.enviar(comunicacion);
        });

        assertEquals("Canal no soportado", exception.getMessage());
        verify(repositoryPort, never()).save(any());
    }

    @Test
    void getBandeja_DeberiaRetornarListaDeComunicaciones() {
        // Arrange
        String usuarioId = "user123";
        List<Comunicacion> mockLista = Arrays.asList(new Comunicacion(), new Comunicacion());
        when(repositoryPort.findByDestinatario(usuarioId)).thenReturn(mockLista);

        // Act
        List<Comunicacion> resultado = comunicacionService.getBandeja(usuarioId);

        // Assert
        assertEquals(2, resultado.size());
        verify(repositoryPort, times(1)).findByDestinatario(usuarioId);
    }

    @Test
    void getMensaje_DeberiaRetornarOptionalConComunicacion() {
        // Arrange
        Long mensajeId = 1L;
        Comunicacion mockComunicacion = new Comunicacion();
        when(repositoryPort.findById(mensajeId)).thenReturn(Optional.of(mockComunicacion));

        // Act
        Optional<Comunicacion> resultado = comunicacionService.getMensaje(mensajeId);

        // Assert
        assertTrue(resultado.isPresent());
        verify(repositoryPort, times(1)).findById(mensajeId);
    }

    @Test
    void marcarLeido_DeberiaInvocarUpdateLeidoEnElPuerto() {
        // Arrange
        Long mensajeId = 1L;
        Comunicacion mockComunicacion = Comunicacion.builder().id(mensajeId).leido(true).build();
        when(repositoryPort.updateLeido(mensajeId, true)).thenReturn(mockComunicacion);

        // Act
        Comunicacion resultado = comunicacionService.marcarLeido(mensajeId);

        // Assert
        assertTrue(resultado.isLeido());
        verify(repositoryPort, times(1)).updateLeido(mensajeId, true);
    }

    @Test
    void obtenerDestinatarios_DeberiaRetornarListaDeStrings() {
        // Arrange
        List<String> mockDestinatarios = Arrays.asList("user1", "user2");
        when(repositoryPort.findAllDestinatarios()).thenReturn(mockDestinatarios);

        // Act
        List<String> resultado = comunicacionService.obtenerDestinatarios();

        // Assert
        assertEquals(2, resultado.size());
        verify(repositoryPort, times(1)).findAllDestinatarios();
    }
}