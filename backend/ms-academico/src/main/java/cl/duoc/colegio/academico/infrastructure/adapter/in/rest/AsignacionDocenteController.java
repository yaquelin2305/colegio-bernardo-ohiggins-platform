package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.AsignacionDocenteRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.AsignaturaRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.CursoRepositoryPort;
import cl.duoc.colegio.academico.domain.model.AsignacionDocente;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.AsignacionDocenteRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.AsignacionDocenteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asignacion-docente")
@Tag(name = "Asignación Docente", description = "Asignación de docentes a cursos y asignaturas")
public class AsignacionDocenteController {

    private final AsignacionDocenteRepositoryPort asignacionRepository;
    private final CursoRepositoryPort cursoRepository;
    private final AsignaturaRepositoryPort asignaturaRepository;

    public AsignacionDocenteController(AsignacionDocenteRepositoryPort asignacionRepository,
                                        CursoRepositoryPort cursoRepository,
                                        AsignaturaRepositoryPort asignaturaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.cursoRepository = cursoRepository;
        this.asignaturaRepository = asignaturaRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todas las asignaciones de docentes")
    public ResponseEntity<List<AsignacionDocenteResponse>> listar() {
        List<AsignacionDocenteResponse> result = asignacionRepository.buscarTodas()
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una asignación de docente")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asignacionRepository.buscarPorId(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada: id=" + id));
        asignacionRepository.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Asignar un docente a un curso y asignatura")
    public ResponseEntity<AsignacionDocenteResponse> asignar(@Valid @RequestBody AsignacionDocenteRequest request) {

        if (!cursoRepository.existePorId(request.getCursoId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Curso no encontrado: id=" + request.getCursoId());
        }
        if (!asignaturaRepository.existePorId(request.getAsignaturaId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Asignatura no encontrada: id=" + request.getAsignaturaId());
        }
        if (asignacionRepository.existePorDocenteCursoAsignatura(
                request.getDocenteUuid(), request.getCursoId(), request.getAsignaturaId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El docente ya está asignado a este curso y asignatura");
        }

        AsignacionDocente nueva = new AsignacionDocente(
                null,
                request.getDocenteUuid(),
                request.getCursoId(),
                request.getAsignaturaId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(asignacionRepository.guardar(nueva)));
    }

    private AsignacionDocenteResponse toResponse(AsignacionDocente a) {
        return AsignacionDocenteResponse.builder()
                .id(a.getId())
                .docenteUuid(a.getDocenteUuid())
                .cursoId(a.getCursoId())
                .asignaturaId(a.getAsignaturaId())
                .build();
    }
}
