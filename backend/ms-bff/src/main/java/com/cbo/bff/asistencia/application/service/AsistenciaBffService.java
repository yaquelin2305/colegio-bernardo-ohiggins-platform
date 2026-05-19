package com.cbo.bff.asistencia.application.service;

import com.cbo.bff.asistencia.domain.dto.AnotacionBffDTO;
import com.cbo.bff.asistencia.domain.dto.AsistenciaRequestBffDTO;
import com.cbo.bff.asistencia.domain.dto.EstudianteAsistenciaBffDTO;
import com.cbo.bff.asistencia.domain.dto.HistorialAsistenciaBffDTO;
import com.cbo.bff.asistencia.domain.dto.InasistenciaBffDTO;
import com.cbo.bff.asistencia.domain.dto.JustificacionBffRequestDTO;
import com.cbo.bff.asistencia.domain.dto.ResumenAsistenciaBffDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.AsistenciaFeignClient;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.AnotacionMsRequestDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.AnotacionMsResponseDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.AsistenciaMsRequestDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.AsistenciaMsResponseDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.JustificacionMsRequestDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.ResumenAsistenciaMsDTO;
import com.cbo.bff.gestionacademica.infrastructure.output.feign.UsuarioFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AsistenciaBffService {

    private final AsistenciaFeignClient asistenciaFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;

    public List<EstudianteAsistenciaBffDTO> registrar(List<AsistenciaRequestBffDTO> request) {
        List<AsistenciaMsRequestDTO> msRequest = request.stream()
                .map(r -> AsistenciaMsRequestDTO.builder()
                        .estudianteId(r.getEstudianteId())
                        .cursoId(r.getCursoId())
                        .estado(r.getEstado())
                        .observacion(r.getObservacion())
                        .fecha(r.getFecha())
                        .build())
                .toList();
        Map<String, String> cache = new HashMap<>();
        return asistenciaFeignClient.registrar(msRequest).stream()
                .map(ms -> toEstudianteBffDTO(ms, cache))
                .toList();
    }

    public List<EstudianteAsistenciaBffDTO> getPorCurso(String cursoId, LocalDate fecha) {
        Map<String, String> cache = new HashMap<>();
        return asistenciaFeignClient.getPorCurso(cursoId, fecha).stream()
                .map(ms -> toEstudianteBffDTO(ms, cache))
                .toList();
    }

    public List<HistorialAsistenciaBffDTO> getPorEstudiante(String estudianteId) {
        return asistenciaFeignClient.getPorEstudiante(estudianteId).stream()
                .map(this::toHistorialBffDTO)
                .toList();
    }

    public ResumenAsistenciaBffDTO getResumen(String cursoId, LocalDate fecha) {
        return toResumenBffDTO(asistenciaFeignClient.getResumen(cursoId, fecha));
    }

    public List<InasistenciaBffDTO> getInasistencias() {
        Map<String, String> cache = new HashMap<>();
        return asistenciaFeignClient.getInasistencias().stream()
                .map(ms -> toInasistenciaBffDTO(ms, cache))
                .toList();
    }

    public InasistenciaBffDTO justificar(Long id, JustificacionBffRequestDTO request) {
        JustificacionMsRequestDTO msRequest = JustificacionMsRequestDTO.builder()
                .motivo(request.getMotivo())
                .build();
        return toInasistenciaBffDTO(asistenciaFeignClient.justificar(id, msRequest), new HashMap<>());
    }

    private EstudianteAsistenciaBffDTO toEstudianteBffDTO(AsistenciaMsResponseDTO ms, Map<String, String> cache) {
        String uuid = ms.getEstudianteId();
        String nombre = cache.computeIfAbsent(uuid, this::resolverNombre);
        return EstudianteAsistenciaBffDTO.builder()
                .id(ms.getId())
                .estudianteId(uuid)
                .nombre(nombre != null ? nombre : uuid)
                .curso(ms.getCursoId())
                .estado(ms.getEstado() != null ? ms.getEstado().toLowerCase() : null)
                .hora(null)
                .build();
    }

    private String resolverNombre(String uuid) {
        try {
            Map<String, Object> res = usuarioFeignClient.obtenerNombre(uuid);
            Object n = res != null ? res.get("nombreCompleto") : null;
            return n != null ? n.toString() : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private HistorialAsistenciaBffDTO toHistorialBffDTO(AsistenciaMsResponseDTO ms) {
        return HistorialAsistenciaBffDTO.builder()
                .id(ms.getId())
                .fecha(ms.getFecha())
                .estado(ms.getEstado() != null ? ms.getEstado().toLowerCase() : null)
                .anotacion(ms.getObservacion())
                .build();
    }

    private InasistenciaBffDTO toInasistenciaBffDTO(AsistenciaMsResponseDTO ms, Map<String, String> cache) {
        String uuid = ms.getEstudianteId();
        String nombre = cache.computeIfAbsent(uuid, this::resolverNombre);
        return InasistenciaBffDTO.builder()
                .id(ms.getId())
                .fecha(ms.getFecha())
                .alumno(nombre != null ? nombre : uuid)
                .curso(ms.getCursoId())
                .justificada("JUSTIFICADO".equalsIgnoreCase(ms.getEstado()))
                .build();
    }

    public AnotacionBffDTO guardarAnotacion(AnotacionBffDTO request) {
        AnotacionMsRequestDTO msRequest = AnotacionMsRequestDTO.builder()
                .estudianteId(request.getEstudianteId())
                .tipo(request.getTipo())
                .descripcion(request.getDescripcion())
                .build();
        return toAnotacionBffDTO(asistenciaFeignClient.guardarAnotacion(msRequest));
    }

    public List<AnotacionBffDTO> getAnotacionesPorEstudiante(String estudianteId) {
        return asistenciaFeignClient.getAnotacionesPorEstudiante(estudianteId)
                .stream().map(this::toAnotacionBffDTO).toList();
    }

    private AnotacionBffDTO toAnotacionBffDTO(AnotacionMsResponseDTO ms) {
        return AnotacionBffDTO.builder()
                .id(ms.getId())
                .estudianteId(ms.getEstudianteId())
                .tipo(ms.getTipo())
                .descripcion(ms.getDescripcion())
                .fecha(ms.getFecha())
                .build();
    }

    private ResumenAsistenciaBffDTO toResumenBffDTO(ResumenAsistenciaMsDTO ms) {
        return ResumenAsistenciaBffDTO.builder()
                .total(ms.getTotal())
                .presentes(ms.getTotalPresentes())
                .ausentes(ms.getTotalAusentes())
                .totalJustificados(ms.getTotalJustificados())
                .porcentaje(ms.getPorcentajeAsistencia())
                .build();
    }
}
