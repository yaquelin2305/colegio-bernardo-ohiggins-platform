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

/**
 * POST /api/v1/matriculas/matricular
 *
 * Valida integridad referencial:
 *  - cursoId debe existir
 *  - El estudiante no debe estar ya matriculado en el mismo curso
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

        // ── Integridad referencial ────────────────────────────────────────────
        if (!cursoRepository.existePorId(request.getCursoId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Curso no encontrado: id=" + request.getCursoId());
        }
        if (matriculaRepository.existePorUsuarioUuidYCursoId(
                request.getUsuarioUuid(), request.getCursoId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El estudiante ya está matriculado en este curso");
        }
        // ─────────────────────────────────────────────────────────────────────

        Matricula nueva = new Matricula(null, request.getUsuarioUuid(), request.getCursoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaRepository.guardar(nueva));
    }
}
