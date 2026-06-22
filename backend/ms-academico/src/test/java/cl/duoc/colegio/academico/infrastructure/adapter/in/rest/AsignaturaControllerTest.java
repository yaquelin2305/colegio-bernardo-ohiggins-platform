package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.AsignaturaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Asignatura;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.AsignaturaRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.AsignaturaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignaturaControllerTest {

    @Mock
    private AsignaturaRepositoryPort repo;

    @InjectMocks
    private AsignaturaController controller;

    @Test
    void crear_retorna201() {
        AsignaturaRequest req = new AsignaturaRequest();
        req.setNombre("Matemáticas");
        req.setHorasSemanales(6);
        when(repo.guardar(any())).thenReturn(new Asignatura(1L, "Matemáticas", 6));

        ResponseEntity<AsignaturaResponse> resp = controller.crear(req);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody().getNombre()).isEqualTo("Matemáticas");
    }

    @Test
    void listar_retornaLista() {
        when(repo.listarTodas()).thenReturn(List.of(new Asignatura(1L, "Lenguaje", 5)));

        ResponseEntity<List<AsignaturaResponse>> resp = controller.listar();

        assertThat(resp.getBody()).hasSize(1);
    }
}
