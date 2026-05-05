package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.CursoRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.MatriculaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Matricula;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.MatriculaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * POST /api/v1/matriculas/matricular
 * GET  /api/v1/matriculas/curso/{cursoId}/estudiantes
 */
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
    public ResponseEntity<Matricula> matricular(@Valid @RequestBody MatriculaRequest request) {
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
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaRepository.guardar(nueva));
    }

    /**
     * Lista los UUIDs de los estudiantes matriculados en un curso.
     * Consumido por ListadoEstudiantesCursoPage del front.
     *
     * GET /api/v1/matriculas/curso/{cursoId}/estudiantes
     */
    @GetMapping("/curso/{cursoId}/estudiantes")
    @Operation(summary = "Listar estudiantes matriculados en un curso")
    public ResponseEntity<List<Map<String, Object>>> listarEstudiantesPorCurso(
            @PathVariable Long cursoId) {

        if (!cursoRepository.existePorId(cursoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Curso no encontrado: id=" + cursoId);
        }

        List<Map<String, Object>> resultado = matriculaRepository.buscarPorCursoId(cursoId)
                .stream()
                .map(m -> Map.<String, Object>of(
                        "matriculaId",  m.getId(),
                        "usuarioUuid",  m.getUsuarioUuid().toString(),
                        "cursoId",      m.getCursoId()
                ))
                .toList();

        return ResponseEntity.ok(resultado);
    }
}
