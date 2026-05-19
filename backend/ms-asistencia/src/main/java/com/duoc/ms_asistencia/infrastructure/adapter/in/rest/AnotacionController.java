package com.duoc.ms_asistencia.infrastructure.adapter.in.rest;

import com.duoc.ms_asistencia.domain.model.Anotacion;
import com.duoc.ms_asistencia.domain.port.in.AnotacionUseCase;
import com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto.AnotacionRequestDTO;
import com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto.AnotacionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencia/anotaciones")
public class AnotacionController {

    private final AnotacionUseCase useCase;

    public AnotacionController(AnotacionUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<AnotacionResponseDTO> guardar(@RequestBody AnotacionRequestDTO request) {
        Anotacion anotacion = Anotacion.builder()
                .estudianteId(request.getEstudianteId())
                .tipo(request.getTipo())
                .descripcion(request.getDescripcion())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(useCase.guardar(anotacion)));
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<AnotacionResponseDTO>> listar(@PathVariable String estudianteId) {
        List<AnotacionResponseDTO> lista = useCase.listarPorEstudiante(estudianteId)
                .stream().map(this::toDTO).toList();
        return ResponseEntity.ok(lista);
    }

    private AnotacionResponseDTO toDTO(Anotacion a) {
        AnotacionResponseDTO dto = new AnotacionResponseDTO();
        dto.setId(a.getId());
        dto.setEstudianteId(a.getEstudianteId());
        dto.setTipo(a.getTipo());
        dto.setDescripcion(a.getDescripcion());
        dto.setFecha(a.getFecha());
        return dto;
    }
}
