#set( $symbol_dollar = '$' )
package ${package}.infrastructure.adapter.in.rest;

import ${package}.application.dto.EjemploResponse;
import ${package}.domain.model.Ejemplo;
import ${package}.domain.port.in.EjemploUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ejemplos")
@RequiredArgsConstructor
public class EjemploController {

    private final EjemploUseCase ejemploUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<EjemploResponse> obtener(@PathVariable Long id) {
        Ejemplo ejemplo = ejemploUseCase.obtenerPorId(id)
                .orElseThrow();
        return ResponseEntity.ok(EjemploResponse.fromDomain(ejemplo));
    }

    @GetMapping
    public ResponseEntity<List<EjemploResponse>> listar() {
        List<EjemploResponse> response = ejemploUseCase.listarTodos()
                .stream()
                .map(EjemploResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EjemploResponse> crear(@RequestBody Ejemplo ejemplo) {
        Ejemplo creado = ejemploUseCase.crear(ejemplo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EjemploResponse.fromDomain(creado));
    }
}
