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
        Grade grade = new Grade(null, request.getStudentId(), request.getAsignatura(),
                request.getNota(), request.getTipo(), request.getFecha(), request.getDescripcion());
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeUseCase.registrarNota(grade));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener nota por ID")
    public ResponseEntity<Grade> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(gradeUseCase.obtenerNotaPorId(id));
    }

    @GetMapping("/estudiante/{studentId}")
    @Operation(summary = "Listar notas de un estudiante")
    public ResponseEntity<List<Grade>> listarPorEstudiante(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeUseCase.listarNotasPorEstudiante(studentId));
    }

    @GetMapping("/estudiante/{studentId}/asignatura/{asignatura}")
    @Operation(summary = "Listar notas de un estudiante por asignatura")
    public ResponseEntity<List<Grade>> listarPorEstudianteYAsignatura(
            @PathVariable Long studentId,
            @PathVariable String asignatura) {
        return ResponseEntity.ok(gradeUseCase.listarNotasPorEstudianteYAsignatura(studentId, asignatura));
    }

    @GetMapping("/estudiante/{studentId}/promedio")
    @Operation(summary = "Calcular promedio general de un estudiante")
    public ResponseEntity<Map<String, Double>> calcularPromedio(@PathVariable Long studentId) {
        double promedio = gradeUseCase.calcularPromedioEstudiante(studentId);
        return ResponseEntity.ok(Map.of("promedio", promedio));
    }

    @GetMapping("/estudiante/{studentId}/promedio/asignatura/{asignatura}")
    @Operation(summary = "Calcular promedio de un estudiante por asignatura")
    public ResponseEntity<Map<String, Double>> calcularPromedioAsignatura(
            @PathVariable Long studentId,
            @PathVariable String asignatura) {
        double promedio = gradeUseCase.calcularPromedioEstudiantePorAsignatura(studentId, asignatura);
        return ResponseEntity.ok(Map.of("promedio", promedio, "studentId",
                Double.valueOf(studentId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nota")
    public ResponseEntity<Grade> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody GradeRequest request) {
        Grade grade = new Grade(id, request.getStudentId(), request.getAsignatura(),
                request.getNota(), request.getTipo(), request.getFecha(), request.getDescripcion());
        return ResponseEntity.ok(gradeUseCase.actualizarNota(id, grade));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar nota")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gradeUseCase.eliminarNota(id);
        return ResponseEntity.noContent().build();
    }
}
