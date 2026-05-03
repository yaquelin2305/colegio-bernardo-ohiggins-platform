package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.AsignaturaRepositoryPort;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CalificacionesContractDto;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CalificacionesRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.GradeEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.GradeJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PUT /api/v1/calificaciones/guardar
 *
 * Cumple el contrato del ERD: recibe nota_1, nota_2, nota_3,
 * calcula el promedio en servicio (frescura garantizada) y persiste la estructura fija.
 *
 * Si ya existe un registro para ese estudiante+asignatura, hace UPDATE.
 * Si no existe, crea uno nuevo (upsert semántico).
 */
@RestController
@RequestMapping("/api/v1/calificaciones")
@Tag(name = "Calificaciones", description = "Gestión de notas según contrato ERD")
public class CalificacionesController {

    private final GradeJpaRepository gradeRepository;
    private final AsignaturaRepositoryPort asignaturaRepository;

    public CalificacionesController(GradeJpaRepository gradeRepository,
                                     AsignaturaRepositoryPort asignaturaRepository) {
        this.gradeRepository = gradeRepository;
        this.asignaturaRepository = asignaturaRepository;
    }

    @PutMapping("/guardar")
    @Operation(summary = "Guardar/actualizar calificaciones de un estudiante en una asignatura")
    public ResponseEntity<CalificacionesContractDto> guardar(
            @Valid @RequestBody CalificacionesRequest request) {

        // ── Integridad referencial ────────────────────────────────────────────
        if (!asignaturaRepository.existePorId(request.getAsignaturaId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Asignatura no encontrada: id=" + request.getAsignaturaId());
        }
        // ─────────────────────────────────────────────────────────────────────

        // Buscar registro existente (upsert)
        Optional<GradeEntity> existente = gradeRepository
                .findByUsuarioUuidAndAsignaturaId(request.getUsuarioUuid(), request.getAsignaturaId());

        // Calcular promedio en memoria con las notas presentes
        List<Double> notasPresentes = new ArrayList<>();
        notasPresentes.add(request.getNota1());
        if (request.getNota2() != null) notasPresentes.add(request.getNota2());
        if (request.getNota3() != null) notasPresentes.add(request.getNota3());

        double promedio = notasPresentes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        double promedioRedondeado = Math.round(promedio * 10.0) / 10.0;

        // Construir o actualizar entidad
        GradeEntity entity = existente.orElse(GradeEntity.builder()
                .usuarioUuid(request.getUsuarioUuid())
                .asignaturaId(request.getAsignaturaId())
                .build());

        entity.setNota1(request.getNota1());
        entity.setNota2(request.getNota2());
        entity.setNota3(request.getNota3());
        entity.setPromedio(promedioRedondeado);

        gradeRepository.save(entity);

        // Retornar DTO de contrato
        CalificacionesContractDto response = CalificacionesContractDto.from(
                request.getUsuarioUuid(),
                request.getAsignaturaId(),
                notasPresentes
        );

        HttpStatus status = existente.isPresent() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/estudiante/{usuarioUuid}/asignatura/{asignaturaId}")
    @Operation(summary = "Obtener calificaciones de un estudiante en una asignatura")
    public ResponseEntity<CalificacionesContractDto> obtener(
            @PathVariable java.util.UUID usuarioUuid,
            @PathVariable Long asignaturaId) {

        GradeEntity entity = gradeRepository
                .findByUsuarioUuidAndAsignaturaId(usuarioUuid, asignaturaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontraron calificaciones para ese estudiante y asignatura"));

        List<Double> notas = new ArrayList<>();
        if (entity.getNota1() != null) notas.add(entity.getNota1());
        if (entity.getNota2() != null) notas.add(entity.getNota2());
        if (entity.getNota3() != null) notas.add(entity.getNota3());

        return ResponseEntity.ok(CalificacionesContractDto.from(usuarioUuid, asignaturaId, notas));
    }

    /**
     * Listar todas las calificaciones de un estudiante (para el BFF/boletín).
     * GET /api/v1/calificaciones/estudiante/{usuarioUuid}
     */
    @GetMapping("/estudiante/{usuarioUuid}")
    @Operation(summary = "Listar todas las calificaciones de un estudiante (usado por BFF para el boletín)")
    public ResponseEntity<List<CalificacionesContractDto>> listarPorEstudiante(
            @PathVariable java.util.UUID usuarioUuid) {

        List<CalificacionesContractDto> resultado = gradeRepository
                .findByUsuarioUuid(usuarioUuid)
                .stream()
                .map(entity -> {
                    List<Double> notas = new ArrayList<>();
                    if (entity.getNota1() != null) notas.add(entity.getNota1());
                    if (entity.getNota2() != null) notas.add(entity.getNota2());
                    if (entity.getNota3() != null) notas.add(entity.getNota3());
                    return CalificacionesContractDto.from(usuarioUuid, entity.getAsignaturaId(), notas);
                })
                .toList();

        return ResponseEntity.ok(resultado);
    }
}
