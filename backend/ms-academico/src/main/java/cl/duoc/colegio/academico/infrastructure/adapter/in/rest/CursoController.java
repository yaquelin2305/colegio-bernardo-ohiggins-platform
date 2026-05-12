package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.CursoRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Curso;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CursoRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CursoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cursos")
@Tag(name = "Cursos", description = "Gestión de cursos académicos")
public class CursoController {

    private final CursoRepositoryPort cursoRepository;

    public CursoController(CursoRepositoryPort cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear un nuevo curso")
    public ResponseEntity<CursoResponse> crear(@Valid @RequestBody CursoRequest request) {
        Curso nuevo = new Curso(null, request.getNombre(),
                request.getAnioEscolar(), request.getProfesorJefeUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(cursoRepository.guardar(nuevo)));
    }

    @GetMapping
    @Operation(summary = "Listar todos los cursos")
    public ResponseEntity<List<CursoResponse>> listar() {
        return ResponseEntity.ok(cursoRepository.listarTodos().stream()
                .map(this::toResponse).toList());
    }

    private CursoResponse toResponse(Curso c) {
        return CursoResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .anioEscolar(c.getAnioEscolar())
                .profesorJefeUuid(c.getProfesorJefeUuid())
                .build();
    }
}
