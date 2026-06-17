package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.AsignaturaRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.GradeRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.MatriculaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.GradeContract;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CalificacionesContractDto;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.CalificacionesRequest;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calificaciones")
@Tag(name = "Calificaciones", description = "Gestión de notas según contrato ERD")
public class CalificacionesController {

    private final GradeRepositoryPort gradeRepository;
    private final AsignaturaRepositoryPort asignaturaRepository;
    private final MatriculaRepositoryPort matriculaRepository;

    public CalificacionesController(GradeRepositoryPort gradeRepository,
                                     AsignaturaRepositoryPort asignaturaRepository,
                                     MatriculaRepositoryPort matriculaRepository) {
        this.gradeRepository = gradeRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @PutMapping("/guardar")
    @Operation(summary = "Guardar/actualizar calificaciones de un estudiante en una asignatura")
    public ResponseEntity<CalificacionesContractDto> guardar(
            @Valid @RequestBody CalificacionesRequest request) {

        if (!asignaturaRepository.existePorId(request.getAsignaturaId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Asignatura no encontrada: id=" + request.getAsignaturaId());
        }

        List<Double> notasPresentes = new ArrayList<>();
        notasPresentes.add(request.getNota1());
        if (request.getNota2() != null) notasPresentes.add(request.getNota2());
        if (request.getNota3() != null) notasPresentes.add(request.getNota3());

        double promedioRedondeado = Math.round(
                notasPresentes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0) * 10.0) / 10.0;

        Optional<GradeContract> existente = gradeRepository
                .buscarContratoPorUsuarioUuidYAsignaturaId(request.getUsuarioUuid(), request.getAsignaturaId());

        GradeContract contrato = new GradeContract(
                request.getUsuarioUuid(),
                request.getAsignaturaId(),
                request.getNota1(),
                request.getNota2(),
                request.getNota3(),
                promedioRedondeado);

        gradeRepository.guardarContrato(contrato);

        HttpStatus status = existente.isPresent() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(
                CalificacionesContractDto.from(request.getUsuarioUuid(), request.getAsignaturaId(), notasPresentes));
    }

    @GetMapping("/curso/{cursoId}/asignatura/{asignaturaId}")
    @Operation(summary = "Listar calificaciones de todos los estudiantes de un curso en una asignatura")
    public ResponseEntity<List<CalificacionesContractDto>> listarPorCursoYAsignatura(
            @PathVariable Long cursoId,
            @PathVariable Long asignaturaId) {

        List<UUID> estudiantesUuids = matriculaRepository.buscarPorCursoId(cursoId)
                .stream()
                .map(m -> m.getUsuarioUuid())
                .toList();

        List<CalificacionesContractDto> resultado = estudiantesUuids.stream()
                .map(uuid -> gradeRepository
                        .buscarContratoPorUsuarioUuidYAsignaturaId(uuid, asignaturaId)
                        .map(CalificacionesContractDto::from)
                        .orElse(CalificacionesContractDto.from(uuid, asignaturaId, List.of())))
                .toList();

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/estudiante/{usuarioUuid}/asignatura/{asignaturaId}")
    @Operation(summary = "Obtener calificaciones de un estudiante en una asignatura")
    public ResponseEntity<CalificacionesContractDto> obtener(
            @PathVariable UUID usuarioUuid,
            @PathVariable Long asignaturaId) {

        GradeContract contrato = gradeRepository
                .buscarContratoPorUsuarioUuidYAsignaturaId(usuarioUuid, asignaturaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontraron calificaciones para ese estudiante y asignatura"));

        return ResponseEntity.ok(CalificacionesContractDto.from(contrato));
    }

    @GetMapping("/estudiante/{usuarioUuid}")
    @Operation(summary = "Listar todas las calificaciones de un estudiante (BFF/boletín)")
    public ResponseEntity<List<CalificacionesContractDto>> listarPorEstudiante(
            @PathVariable UUID usuarioUuid) {

        List<CalificacionesContractDto> resultado = gradeRepository
                .buscarContratosPorUsuarioUuid(usuarioUuid)
                .stream()
                .map(CalificacionesContractDto::from)
                .toList();

        return ResponseEntity.ok(resultado);
    }
}
