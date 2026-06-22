package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.CursoRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.MatriculaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Matricula;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.MatriculaRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.MatriculaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatriculaControllerTest {

    @Mock
    private MatriculaRepositoryPort matriculaRepo;

    @Mock
    private CursoRepositoryPort cursoRepo;

    @InjectMocks
    private MatriculaController controller;

    private UUID uuid = UUID.randomUUID();

    @Test
    void listarTodas_retornaLista() {
        when(matriculaRepo.buscarTodas()).thenReturn(List.of(new Matricula(1L, uuid, 10L)));

        ResponseEntity<List<MatriculaResponse>> resp = controller.listarTodas();

        assertThat(resp.getBody()).hasSize(1);
    }

    @Test
    void matricular_exitoso_retorna201() {
        MatriculaRequest req = new MatriculaRequest();
        req.setUsuarioUuid(uuid);
        req.setCursoId(10L);
        when(cursoRepo.existePorId(10L)).thenReturn(true);
        when(matriculaRepo.existePorUsuarioUuidYCursoId(uuid, 10L)).thenReturn(false);
        when(matriculaRepo.guardar(any())).thenReturn(new Matricula(1L, uuid, 10L));

        ResponseEntity<MatriculaResponse> resp = controller.matricular(req);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void matricular_cursoNoExiste_lanza422() {
        MatriculaRequest req = new MatriculaRequest();
        req.setUsuarioUuid(uuid);
        req.setCursoId(99L);
        when(cursoRepo.existePorId(99L)).thenReturn(false);

        assertThatThrownBy(() -> controller.matricular(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("422");
    }

    @Test
    void matricular_yaMatriculado_lanza409() {
        MatriculaRequest req = new MatriculaRequest();
        req.setUsuarioUuid(uuid);
        req.setCursoId(10L);
        when(cursoRepo.existePorId(10L)).thenReturn(true);
        when(matriculaRepo.existePorUsuarioUuidYCursoId(uuid, 10L)).thenReturn(true);

        assertThatThrownBy(() -> controller.matricular(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409");
    }

    @Test
    void listarPorEstudiante_retornaLista() {
        when(matriculaRepo.buscarPorUsuarioUuid(uuid)).thenReturn(List.of(new Matricula(1L, uuid, 10L)));

        ResponseEntity<List<MatriculaResponse>> resp = controller.listarPorEstudiante(uuid);

        assertThat(resp.getBody()).hasSize(1);
    }

    @Test
    void listarEstudiantesPorCurso_retornaLista() {
        when(cursoRepo.existePorId(10L)).thenReturn(true);
        when(matriculaRepo.buscarPorCursoId(10L)).thenReturn(List.of(new Matricula(1L, uuid, 10L)));

        ResponseEntity<List<MatriculaResponse>> resp = controller.listarEstudiantesPorCurso(10L);

        assertThat(resp.getBody()).hasSize(1);
    }
}
