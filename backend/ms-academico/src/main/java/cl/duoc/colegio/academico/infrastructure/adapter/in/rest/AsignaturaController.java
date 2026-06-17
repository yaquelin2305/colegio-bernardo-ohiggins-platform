package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.AsignaturaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Asignatura;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.AsignaturaRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.AsignaturaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asignaturas")
@Tag(name = "Asignaturas", description = "Gestión de asignaturas")
public class AsignaturaController {

    private final AsignaturaRepositoryPort asignaturaRepository;

    public AsignaturaController(AsignaturaRepositoryPort asignaturaRepository) {
        this.asignaturaRepository = asignaturaRepository;
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear una nueva asignatura")
    public ResponseEntity<AsignaturaResponse> crear(@Valid @RequestBody AsignaturaRequest request) {
        Asignatura nueva = new Asignatura(null, request.getNombre(), request.getHorasSemanales());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(asignaturaRepository.guardar(nueva)));
    }

    @GetMapping
    @Operation(summary = "Listar todas las asignaturas")
    public ResponseEntity<List<AsignaturaResponse>> listar() {
        return ResponseEntity.ok(asignaturaRepository.listarTodas().stream()
                .map(this::toResponse).toList());
    }

    private AsignaturaResponse toResponse(Asignatura a) {
        return AsignaturaResponse.builder()
                .id(a.getId())
                .nombre(a.getNombre())
                .horasSemanales(a.getHorasSemanales())
                .build();
    }
}
