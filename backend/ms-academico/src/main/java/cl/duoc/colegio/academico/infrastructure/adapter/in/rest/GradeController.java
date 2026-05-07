package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.in.GradeUseCase;
import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.GradeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador REST para Notas académicas.
 */
@RestController
@RequestMapping("/api/v1/notas")
@Tag(name = "Notas", description = "Gestión de notas académicas")
public class GradeController {

    private final GradeUseCase gradeUseCase;

    public GradeController(GradeUseCase gradeUseCase) {
        this.gradeUseCase = gradeUseCase;
    }

    @PostMapping
    @Operation(summary = "Registrar nota")
    public ResponseEntity<Grade> registrar(@Valid @RequestBody GradeRequest request) {
        Grade grade = new Grade(null, request.getUsuarioUuid(), request.getAsignaturaId(),
                request.getNota(), request.getTipo(), request.getDescripcion());
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeUseCase.registrarNota(grade));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener nota por ID")
    public ResponseEntity<Grade> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(gradeUseCase.obtenerNotaPorId(id));
    }

    @GetMapping("/estudiante/{usuarioUuid}")
    @Operation(summary = "Listar notas de un estudiante")
    public ResponseEntity<List<Grade>> listarPorEstudiante(@PathVariable UUID usuarioUuid) {
        return ResponseEntity.ok(gradeUseCase.listarNotasPorEstudiante(usuarioUuid));
    }

    @GetMapping("/estudiante/{usuarioUuid}/asignatura/{asignaturaId}")
    @Operation(summary = "Listar notas de un estudiante por asignatura")
    public ResponseEntity<List<Grade>> listarPorEstudianteYAsignatura(
            @PathVariable UUID usuarioUuid,
            @PathVariable Long asignaturaId) {
        return ResponseEntity.ok(gradeUseCase.listarNotasPorEstudianteYAsignatura(usuarioUuid, asignaturaId));
    }

    @GetMapping("/estudiante/{usuarioUuid}/promedio")
    @Operation(summary = "Calcular promedio general de un estudiante")
    public ResponseEntity<Map<String, Double>> calcularPromedio(@PathVariable UUID usuarioUuid) {
        double promedio = gradeUseCase.calcularPromedioEstudiante(usuarioUuid);
        return ResponseEntity.ok(Map.of("promedio", promedio));
    }

    @GetMapping("/estudiante/{usuarioUuid}/promedio/asignatura/{asignaturaId}")
    @Operation(summary = "Calcular promedio de un estudiante por asignatura")
    public ResponseEntity<Map<String, Object>> calcularPromedioAsignatura(
            @PathVariable UUID usuarioUuid,
            @PathVariable Long asignaturaId) {
        double promedio = gradeUseCase.calcularPromedioEstudiantePorAsignatura(usuarioUuid, asignaturaId);
        return ResponseEntity.ok(Map.of("promedio", promedio, "usuarioUuid",
                usuarioUuid.toString()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nota")
    public ResponseEntity<Grade> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody GradeRequest request) {
        Grade grade = new Grade(id, request.getUsuarioUuid(), request.getAsignaturaId(),
                request.getNota(), request.getTipo(), request.getDescripcion());
        return ResponseEntity.ok(gradeUseCase.actualizarNota(id, grade));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar nota")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gradeUseCase.eliminarNota(id);
        return ResponseEntity.noContent().build();
    }
}
