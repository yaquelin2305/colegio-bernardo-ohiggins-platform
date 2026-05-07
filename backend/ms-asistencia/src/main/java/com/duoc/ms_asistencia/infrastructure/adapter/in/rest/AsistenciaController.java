package com.duoc.ms_asistencia.infrastructure.adapter.in.rest;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import com.duoc.ms_asistencia.domain.model.ResumenAsistencia;
import com.duoc.ms_asistencia.domain.port.in.AsistenciaUseCase;
import com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto.AsistenciaRequestDTO;
import com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto.AsistenciaResponseDTO;
import com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto.JustificacionRequestDTO;
import com.duoc.ms_asistencia.infrastructure.adapter.in.rest.dto.ResumenAsistenciaDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/asistencia")
public class AsistenciaController {

    private final AsistenciaUseCase useCase;

    public AsistenciaController(AsistenciaUseCase useCase) {
        this.useCase = useCase;
    }

    // Endpoint para registrar la lista masiva (Requerimiento imagen)
    @PostMapping("/registrar")
    public ResponseEntity<List<AsistenciaResponseDTO>> registrar(@RequestBody List<AsistenciaRequestDTO> requests) {
        List<Asistencia> asistencias = requests.stream().map(request ->
            Asistencia.builder()
                .estudianteId(request.getEstudianteId())
                .cursoId(request.getCursoId())
                .estado(request.getEstado())
                .observacion(request.getObservacion())
                .fecha(request.getFecha() != null ? request.getFecha() : LocalDate.now())
                .build()
        ).collect(Collectors.toList());

        List<Asistencia> guardadas = useCase.registrarLista(asistencias);
        
        List<AsistenciaResponseDTO> response = guardadas.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    // Endpoint para consultar por curso y fecha (Requerimiento imagen)
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<AsistenciaResponseDTO>> getPorCurso(
            @PathVariable String cursoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        LocalDate fechaBusqueda = (fecha != null) ? fecha : LocalDate.now();
        List<AsistenciaResponseDTO> lista = useCase.obtenerPorCursoYFecha(cursoId, fechaBusqueda).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<AsistenciaResponseDTO>> getByEstudiante(@PathVariable String estudianteId) {
        List<AsistenciaResponseDTO> lista = useCase.obtenerPorEstudiante(estudianteId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenAsistenciaDTO> getResumen(
            @RequestParam String cursoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDate fechaBusqueda = fecha != null ? fecha : LocalDate.now();
        ResumenAsistencia resumen = useCase.obtenerResumen(cursoId, fechaBusqueda);
        ResumenAsistenciaDTO dto = toResumenDTO(resumen);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/inasistencias")
    public ResponseEntity<List<AsistenciaResponseDTO>> getInasistencias() {
        List<AsistenciaResponseDTO> lista = useCase.obtenerInasistencias().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PatchMapping("/{id}/justificar")
    public ResponseEntity<AsistenciaResponseDTO> justificar(
            @PathVariable Long id,
            @RequestBody JustificacionRequestDTO request) {
        Asistencia actualizada = useCase.justificarInasistencia(id, request.getMotivo());
        return ResponseEntity.ok(toResponseDTO(actualizada));
    }

    // Mapeo manual tal cual lo tienes en Comunicaciones
    private AsistenciaResponseDTO toResponseDTO(Asistencia dom) {
        AsistenciaResponseDTO dto = new AsistenciaResponseDTO();
        dto.setId(dom.getId());
        dto.setEstudianteId(dom.getEstudianteId());
        dto.setCursoId(dom.getCursoId());
        dto.setFecha(dom.getFecha());
        dto.setEstado(dom.getEstado());
        dto.setObservacion(dom.getObservacion());
        return dto;
    }

    private ResumenAsistenciaDTO toResumenDTO(ResumenAsistencia dom) {
        ResumenAsistenciaDTO dto = new ResumenAsistenciaDTO();
        dto.setCursoId(dom.getCursoId());
        dto.setFecha(dom.getFecha());
        dto.setTotalPresentes(dom.getTotalPresentes());
        dto.setTotalAusentes(dom.getTotalAusentes());
        dto.setTotalJustificados(dom.getTotalJustificados());
        dto.setTotal(dom.getTotal());
        dto.setPorcentajeAsistencia(dom.getPorcentajeAsistencia());
        return dto;
    }
}