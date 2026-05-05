package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.out.AsignaturaRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Asignatura;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.AsignaturaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * POST /api/v1/asignaturas/crear
 * GET  /api/v1/asignaturas
 */
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
    public ResponseEntity<Asignatura> crear(@Valid @RequestBody AsignaturaRequest request) {
        Asignatura nueva = new Asignatura(null, request.getNombre(), request.getHorasSemanales());
        return ResponseEntity.status(HttpStatus.CREATED).body(asignaturaRepository.guardar(nueva));
    }

    @GetMapping
    @Operation(summary = "Listar todas las asignaturas")
    public ResponseEntity<List<Asignatura>> listar() {
        return ResponseEntity.ok(asignaturaRepository.listarTodas());
    }
}
