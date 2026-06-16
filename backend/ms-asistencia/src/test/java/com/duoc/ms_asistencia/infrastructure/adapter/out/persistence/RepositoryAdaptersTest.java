package com.duoc.ms_asistencia.infrastructure.adapter.out.persistence;

import com.duoc.ms_asistencia.domain.model.Anotacion;
import com.duoc.ms_asistencia.domain.model.Asistencia;
import com.duoc.ms_asistencia.infrastructure.adapter.out.persistence.entity.AnotacionEntity;
import com.duoc.ms_asistencia.infrastructure.adapter.out.persistence.entity.AsistenciaEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class RepositoryAdaptersTest {

    private final AsistenciaJpaRepository asistenciaJpa = Mockito.mock(AsistenciaJpaRepository.class);
    private final AnotacionJpaRepository anotacionJpa = Mockito.mock(AnotacionJpaRepository.class);

    private final AsistenciaRepositoryAdapter asistenciaAdapter = new AsistenciaRepositoryAdapter(asistenciaJpa);
    private final AnotacionRepositoryAdapter anotacionAdapter = new AnotacionRepositoryAdapter(anotacionJpa);

    @Test
    void testAsistenciaRepositoryAdapter_Metodos() {
        AsistenciaEntity entity = new AsistenciaEntity();
        entity.setId(1L); entity.setCursoId("C1"); entity.setEstado("PRESENTE"); entity.setFecha(LocalDate.now());

        Mockito.when(asistenciaJpa.saveAll(any())).thenReturn(List.of(entity));
        Mockito.when(asistenciaJpa.save(any())).thenReturn(entity);
        Mockito.when(asistenciaJpa.findByCursoIdAndFecha(any(), any())).thenReturn(List.of(entity));
        Mockito.when(asistenciaJpa.findByEstudianteId(any())).thenReturn(List.of(entity));
        Mockito.when(asistenciaJpa.findByEstadoIn(any())).thenReturn(List.of(entity));
        Mockito.when(asistenciaJpa.findById(1L)).thenReturn(Optional.of(entity));

        Asistencia dom = Asistencia.builder().id(1L).estado("PRESENTE").build();
        
        assertNotNull(asistenciaAdapter.saveAll(List.of(dom)));
        assertNotNull(asistenciaAdapter.save(dom));
        assertEquals(1, asistenciaAdapter.findByCursoIdAndFecha("C1", LocalDate.now()).size());
        assertEquals(1, asistenciaAdapter.findByEstudianteId("E1").size());
        assertEquals(1, asistenciaAdapter.findByEstadoIn(List.of("PRESENTE")).size());
        assertTrue(asistenciaAdapter.findById(1L).isPresent());
    }

    @Test
    void testAnotacionRepositoryAdapter_Metodos() {
        AnotacionEntity entity = new AnotacionEntity();
        entity.setId(1L); entity.setEstudianteId("E1"); entity.setTipo("P");

        Mockito.when(anotacionJpa.save(any())).thenReturn(entity);
        Mockito.when(anotacionJpa.findByEstudianteId("E1")).thenReturn(List.of(entity));

        Anotacion dom = Anotacion.builder().id(1L).estudianteId("E1").build();

        assertNotNull(anotacionAdapter.guardar(dom));
        assertEquals(1, anotacionAdapter.buscarPorEstudiante("E1").size());
    }
}