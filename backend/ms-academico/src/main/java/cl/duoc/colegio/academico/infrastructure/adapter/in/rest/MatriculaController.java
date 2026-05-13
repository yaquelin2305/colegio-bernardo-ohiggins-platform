package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.CursoRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.MatriculaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Matricula;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.MatriculaRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.MatriculaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matriculas")
@Tag(name = "Matrículas", description = "Matrícula de estudiantes en cursos")
public class MatriculaController {

    private final MatriculaRepositoryPort matriculaRepository;
    private final CursoRepositoryPort cursoRepository;

    public MatriculaController(MatriculaRepositoryPort matriculaRepository,
                                CursoRepositoryPort cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = cursoRepository;
    }

    @PostMapping("/matricular")
    @Operation(summary = "Matricular un estudiante en un curso")
    public ResponseEntity<MatriculaResponse> matricular(@Valid @RequestBody MatriculaRequest request) {
        if (!cursoRepository.existePorId(request.getCursoId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Curso no encontrado: id=" + request.getCursoId());
        }
        if (matriculaRepository.existePorUsuarioUuidYCursoId(
                request.getUsuarioUuid(), request.getCursoId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El estudiante ya está matriculado en este curso");
        }
        Matricula nueva = new Matricula(null, request.getUsuarioUuid(), request.getCursoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(matriculaRepository.guardar(nueva)));
    }

    @GetMapping("/curso/{cursoId}/estudiantes")
    @Operation(summary = "Listar estudiantes matriculados en un curso")
    public ResponseEntity<List<MatriculaResponse>> listarEstudiantesPorCurso(
            @PathVariable Long cursoId) {

        if (!cursoRepository.existePorId(cursoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Curso no encontrado: id=" + cursoId);
        }

        List<MatriculaResponse> resultado = matriculaRepository.buscarPorCursoId(cursoId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(resultado);
    }

    private MatriculaResponse toResponse(Matricula m) {
        return MatriculaResponse.builder()
                .id(m.getId())
                .usuarioUuid(m.getUsuarioUuid())
                .cursoId(m.getCursoId())
                .build();
    }
}
