package com.duoc.ms_comunicaciones.infrastructure.adapter.out.persistence;

import com.duoc.ms_comunicaciones.domain.model.Canal;
import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.infrastructure.adapter.out.persistence.entity.CanalEntity;
import com.duoc.ms_comunicaciones.infrastructure.adapter.out.persistence.entity.ComunicacionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacionRepositoryAdapterTest {

    @Mock
    private ComunicacionJpaRepository jpaRepository;

    @InjectMocks
    private ComunicacionRepositoryAdapter repositoryAdapter;

    @Test
    void save_DeberiaMapearYGuardarEntidadCorrectamente() {

        Comunicacion dominioInput =
                Comunicacion.builder()
                        .usuarioId("USER1")
                        .destinatario("569123456")
                        .asunto("Asunto")
                        .mensaje("Mensaje")
                        .tipo("ALERTA")
                        .canal(Canal.WHATSAPP)
                        .leido(false)
                        .build();

        ComunicacionEntity entitySaved =
                new ComunicacionEntity();

        entitySaved.setId(100L);
        entitySaved.setUsuarioId("USER1");
        entitySaved.setDestinatario("569123456");
        entitySaved.setAsunto("Asunto");
        entitySaved.setMensaje("Mensaje");
        entitySaved.setTipo("ALERTA");
        entitySaved.setCanal(CanalEntity.WHATSAPP);
        entitySaved.setLeido(false);

        when(jpaRepository.save(any(
                ComunicacionEntity.class
        ))).thenReturn(entitySaved);

        Comunicacion resultado =
                repositoryAdapter.save(
                        dominioInput
                );

        assertNotNull(resultado);

        assertEquals(
                100L,
                resultado.getId()
        );

        assertEquals(
                "USER1",
                resultado.getUsuarioId()
        );

        assertEquals(
                "569123456",
                resultado.getDestinatario()
        );

        assertEquals(
                Canal.WHATSAPP,
                resultado.getCanal()
        );

        verify(
                jpaRepository,
                times(1)
        ).save(any(
                ComunicacionEntity.class
        ));
    }

    @Test
    void findByDestinatario_DeberiaRetornarListaMapeada() {

        ComunicacionEntity entity =
                new ComunicacionEntity();

        entity.setId(1L);
        entity.setUsuarioId("USER1");
        entity.setDestinatario("correo@test.com");
        entity.setAsunto("Aviso");
        entity.setMensaje("Mensaje");
        entity.setTipo("INFO");
        entity.setCanal(CanalEntity.EMAIL);

        when(jpaRepository.findByDestinatario(
                "correo@test.com"
        )).thenReturn(
                List.of(entity)
        );

        List<Comunicacion> resultado =
                repositoryAdapter.findByDestinatario(
                        "correo@test.com"
                );

        assertEquals(
                1,
                resultado.size()
        );

        assertEquals(
                "correo@test.com",
                resultado.get(0).getDestinatario()
        );

        assertEquals(
                Canal.EMAIL,
                resultado.get(0).getCanal()
        );

    }

    @Test
    void findById_DeberiaRetornarOptionalMapeado() {

        ComunicacionEntity entity =
                new ComunicacionEntity();

        entity.setId(10L);
        entity.setCanal(CanalEntity.SMS);

        when(jpaRepository.findById(
                10L
        )).thenReturn(
                Optional.of(entity)
        );

        Optional<Comunicacion> resultado =
                repositoryAdapter.findById(
                        10L
                );

        assertTrue(
                resultado.isPresent()
        );

        assertEquals(
                10L,
                resultado.get().getId()
        );

        assertEquals(
                Canal.SMS,
                resultado.get().getCanal()
        );

    }

    @Test
    void findById_DeberiaRetornarOptionalVacio() {

        when(jpaRepository.findById(
                50L
        )).thenReturn(
                Optional.empty()
        );

        Optional<Comunicacion> resultado =
                repositoryAdapter.findById(
                        50L
                );

        assertTrue(
                resultado.isEmpty()
        );

    }

    @Test
    void updateLeido_DeberiaModificarEstadoLeido_CuandoExiste() {

        Long id = 1L;

        ComunicacionEntity entity =
                new ComunicacionEntity();

        entity.setId(id);
        entity.setLeido(false);
        entity.setCanal(
                CanalEntity.EMAIL
        );

        when(jpaRepository.findById(
                id
        )).thenReturn(
                Optional.of(entity)
        );

        when(jpaRepository.save(any(
                ComunicacionEntity.class
        ))).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Comunicacion resultado =
                repositoryAdapter.updateLeido(
                        id,
                        true
                );

        assertTrue(
                resultado.isLeido()
        );

        verify(
                jpaRepository
        ).save(entity);

    }

    @Test
    void updateLeido_DeberiaLanzarExcepcion_CuandoNoExiste() {

        when(jpaRepository.findById(
                99L
        )).thenReturn(
                Optional.empty()
        );

        assertThrows(
                RuntimeException.class,
                () -> repositoryAdapter.updateLeido(
                        99L,
                        true
                )
        );

        verify(
                jpaRepository,
                never()
        ).save(any());

    }

    @Test
    void findAllDestinatarios_DeberiaEliminarDuplicados() {

        ComunicacionEntity a =
                new ComunicacionEntity();

        a.setDestinatario(
                "user@test.com"
        );

        ComunicacionEntity b =
                new ComunicacionEntity();

        b.setDestinatario(
                "user@test.com"
        );

        ComunicacionEntity c =
                new ComunicacionEntity();

        c.setDestinatario(
                "otro@test.com"
        );

        when(jpaRepository.findAll())
                .thenReturn(
                        List.of(a,b,c)
                );

        List<String> resultado =
                repositoryAdapter.findAllDestinatarios();

        assertEquals(
                2,
                resultado.size()
        );

        assertTrue(
                resultado.contains(
                        "user@test.com"
                )
        );

        assertTrue(
                resultado.contains(
                        "otro@test.com"
                )
        );

    }

}