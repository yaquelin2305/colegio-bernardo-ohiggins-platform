package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.in.GradeUseCase;
import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.GradeRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.GradeResponse;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.PromedioAsignaturaResponse;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.PromedioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<GradeResponse> registrar(@Valid @RequestBody GradeRequest request) {
        Grade grade = new Grade(null, request.getUsuarioUuid(), request.getAsignaturaId(),
                request.getNota(), request.getTipo(), request.getDescripcion());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(gradeUseCase.registrarNota(grade)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener nota por ID")
    public ResponseEntity<GradeResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(gradeUseCase.obtenerNotaPorId(id)));
    }

    @GetMapping("/estudiante/{usuarioUuid}")
    @Operation(summary = "Listar notas de un estudiante")
    public ResponseEntity<List<GradeResponse>> listarPorEstudiante(@PathVariable UUID usuarioUuid) {
        return ResponseEntity.ok(gradeUseCase.listarNotasPorEstudiante(usuarioUuid).stream()
                .map(this::toResponse).toList());
    }

    @GetMapping("/estudiante/{usuarioUuid}/asignatura/{asignaturaId}")
    @Operation(summary = "Listar notas de un estudiante por asignatura")
    public ResponseEntity<List<GradeResponse>> listarPorEstudianteYAsignatura(
            @PathVariable UUID usuarioUuid,
            @PathVariable Long asignaturaId) {
        return ResponseEntity.ok(gradeUseCase.listarNotasPorEstudianteYAsignatura(usuarioUuid, asignaturaId).stream()
                .map(this::toResponse).toList());
    }

    @GetMapping("/estudiante/{usuarioUuid}/promedio")
    @Operation(summary = "Calcular promedio general de un estudiante")
    public ResponseEntity<PromedioResponse> calcularPromedio(@PathVariable UUID usuarioUuid) {
        double promedio = gradeUseCase.calcularPromedioEstudiante(usuarioUuid);
        return ResponseEntity.ok(PromedioResponse.builder()
                .usuarioUuid(usuarioUuid).promedio(promedio).build());
    }

    @GetMapping("/estudiante/{usuarioUuid}/promedio/asignatura/{asignaturaId}")
    @Operation(summary = "Calcular promedio de un estudiante por asignatura")
    public ResponseEntity<PromedioAsignaturaResponse> calcularPromedioAsignatura(
            @PathVariable UUID usuarioUuid,
            @PathVariable Long asignaturaId) {
        double promedio = gradeUseCase.calcularPromedioEstudiantePorAsignatura(usuarioUuid, asignaturaId);
        return ResponseEntity.ok(PromedioAsignaturaResponse.builder()
                .usuarioUuid(usuarioUuid).asignaturaId(asignaturaId).promedio(promedio).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nota")
    public ResponseEntity<GradeResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody GradeRequest request) {
        Grade grade = new Grade(id, request.getUsuarioUuid(), request.getAsignaturaId(),
                request.getNota(), request.getTipo(), request.getDescripcion());
        return ResponseEntity.ok(toResponse(gradeUseCase.actualizarNota(id, grade)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar nota")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gradeUseCase.eliminarNota(id);
        return ResponseEntity.noContent().build();
    }

    private GradeResponse toResponse(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .usuarioUuid(grade.getUsuarioUuid())
                .asignaturaId(grade.getAsignaturaId())
                .nota(grade.getNota())
                .tipo(grade.getTipo())
                .descripcion(grade.getDescripcion())
                .build();
    }
}
