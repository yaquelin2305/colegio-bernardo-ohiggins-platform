package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.CursoRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Curso;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CursoRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CursoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CursoControllerTest {

    @Mock
    private CursoRepositoryPort cursoRepository;

    @InjectMocks
    private CursoController controller;

    @Test
    void crear_curso_retorna201() {
        CursoRequest req = new CursoRequest();
        req.setNombre("1A");
        req.setAnioEscolar(2026);
        UUID uuid = UUID.randomUUID();
        req.setProfesorJefeUuid(uuid);

        when(cursoRepository.guardar(any(Curso.class))).thenReturn(new Curso(1L, "1A", 2026, uuid));

        ResponseEntity<CursoResponse> resp = controller.crear(req);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody().getNombre()).isEqualTo("1A");
    }

    @Test
    void listar_retornaLista() {
        when(cursoRepository.listarTodos()).thenReturn(List.of(new Curso(1L, "1A", 2026, null)));

        ResponseEntity<List<CursoResponse>> resp = controller.listar();

        assertThat(resp.getBody()).hasSize(1);
    }

    @Test
    void obtenerPorId_existe_retorna200() {
        when(cursoRepository.buscarPorId(1L)).thenReturn(Optional.of(new Curso(1L, "1A", 2026, null)));

        ResponseEntity<CursoResponse> resp = controller.obtenerPorId(1L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
