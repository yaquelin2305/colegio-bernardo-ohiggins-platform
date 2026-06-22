package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.AsignaturaRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.GradeRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.MatriculaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.GradeContract;
import cl.duoc.colegio.academico.domain.model.Matricula;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CalificacionesContractDto;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CalificacionesRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalificacionesController — Pruebas Unitarias")
class CalificacionesControllerTest {

    @Mock
    private GradeRepositoryPort gradeRepository;

    @Mock
    private AsignaturaRepositoryPort asignaturaRepository;

    @Mock
    private MatriculaRepositoryPort matriculaRepository;

    @InjectMocks
    private CalificacionesController controller;

    private UUID uuid = UUID.randomUUID();

    @Test
    void guardar_exitoso_nuevo_retorna201() {
        when(asignaturaRepository.existePorId(5L)).thenReturn(true);
        when(gradeRepository.buscarContratoPorUsuarioUuidYAsignaturaId(uuid, 5L))
                .thenReturn(Optional.empty());

        CalificacionesRequest req = new CalificacionesRequest();
        req.setUsuarioUuid(uuid);
        req.setAsignaturaId(5L);
        req.setNota1(6.0);
        req.setNota2(5.5);
        req.setNota3(null);

        ResponseEntity<CalificacionesContractDto> resp = controller.guardar(req);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
    }

    @Test
    void guardar_exitoso_existente_retorna200() {
        when(asignaturaRepository.existePorId(5L)).thenReturn(true);
        when(gradeRepository.buscarContratoPorUsuarioUuidYAsignaturaId(uuid, 5L))
                .thenReturn(Optional.of(new GradeContract(uuid, 5L, 5.0, null, null, 5.0)));

        CalificacionesRequest req = new CalificacionesRequest();
        req.setUsuarioUuid(uuid);
        req.setAsignaturaId(5L);
        req.setNota1(6.0);
        req.setNota2(null);
        req.setNota3(null);

        ResponseEntity<CalificacionesContractDto> resp = controller.guardar(req);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void guardar_asignaturaNoExiste_lanza422() {
        when(asignaturaRepository.existePorId(99L)).thenReturn(false);

        CalificacionesRequest req = new CalificacionesRequest();
        req.setUsuarioUuid(uuid);
        req.setAsignaturaId(99L);
        req.setNota1(6.0);

        assertThatThrownBy(() -> controller.guardar(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("422");
    }

    @Test
    void listarPorCursoYAsignatura_retornaLista() {
        when(matriculaRepository.buscarPorCursoId(10L)).thenReturn(List.of(
                new Matricula(null, uuid, 10L)));
        when(gradeRepository.buscarContratoPorUsuarioUuidYAsignaturaId(uuid, 5L))
                .thenReturn(Optional.of(new GradeContract(uuid, 5L, 6.0, null, null, 6.0)));

        ResponseEntity<List<CalificacionesContractDto>> resp =
                controller.listarPorCursoYAsignatura(10L, 5L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).hasSize(1);
    }

    @Test
    void obtener_existe_retorna200() {
        when(gradeRepository.buscarContratoPorUsuarioUuidYAsignaturaId(uuid, 5L))
                .thenReturn(Optional.of(new GradeContract(uuid, 5L, 6.0, 5.0, 4.0, 5.0)));

        ResponseEntity<CalificacionesContractDto> resp = controller.obtener(uuid, 5L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().promedio()).isEqualTo(5.0);
    }

    @Test
    void obtener_noExiste_lanza404() {
        when(gradeRepository.buscarContratoPorUsuarioUuidYAsignaturaId(uuid, 5L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.obtener(uuid, 5L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void listarPorEstudiante_retornaLista() {
        when(gradeRepository.buscarContratosPorUsuarioUuid(uuid))
                .thenReturn(List.of(new GradeContract(uuid, 5L, 6.0, 5.5, 5.0, 5.5)));

        ResponseEntity<List<CalificacionesContractDto>> resp = controller.listarPorEstudiante(uuid);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).hasSize(1);
    }
}
