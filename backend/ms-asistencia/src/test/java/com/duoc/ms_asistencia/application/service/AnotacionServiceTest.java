package com.duoc.ms_asistencia.application.service;

import com.duoc.ms_asistencia.domain.model.Anotacion;
import com.duoc.ms_asistencia.domain.port.out.AnotacionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnotacionServiceTest {

    @Mock
    private AnotacionRepositoryPort repositoryPort;

    @InjectMocks
    private AnotacionService anotacionService;

    @Test
    void guardar_DeberiaAsignarFechaActual_SiVieneNula() {
        Anotacion anotacionInput = Anotacion.builder()
                .estudianteId("EST-99")
                .tipo("NEGATIVA")
                .descripcion("No trabaja en clases")
                .fecha(null)
                .build();

        when(repositoryPort.guardar(any(Anotacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Anotacion resultado = anotacionService.guardar(anotacionInput);

        assertNotNull(resultado);
        assertEquals(LocalDate.now(), resultado.getFecha());
        verify(repositoryPort, times(1)).guardar(anotacionInput);
    }

    @Test
    void listarPorEstudiante_DeberiaRetornarListaDelRepositorio() {
        String estudianteId = "EST-100";
        List<Anotacion> listaMock = List.of(
                Anotacion.builder().estudianteId(estudianteId).tipo("POSITIVA").build()
        );
        
        when(repositoryPort.buscarPorEstudiante(estudianteId)).thenReturn(listaMock);

        List<Anotacion> resultado = anotacionService.listarPorEstudiante(estudianteId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("POSITIVA", resultado.get(0).getTipo());
        verify(repositoryPort, times(1)).buscarPorEstudiante(estudianteId);
    }
}