package com.cbo.bff.asistencia.application.service;

import com.cbo.bff.asistencia.domain.dto.AsistenciaRequestBffDTO;
import com.cbo.bff.asistencia.domain.dto.EstudianteAsistenciaBffDTO;
import com.cbo.bff.asistencia.domain.dto.HistorialAsistenciaBffDTO;
import com.cbo.bff.asistencia.domain.dto.InasistenciaBffDTO;
import com.cbo.bff.asistencia.domain.dto.JustificacionBffRequestDTO;
import com.cbo.bff.asistencia.domain.dto.ResumenAsistenciaBffDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.AsistenciaFeignClient;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.AsistenciaMsRequestDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.AsistenciaMsResponseDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.JustificacionMsRequestDTO;
import com.cbo.bff.asistencia.infrastructure.output.feign.dto.ResumenAsistenciaMsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsistenciaBffService {

    private final AsistenciaFeignClient asistenciaFeignClient;

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
        return asistenciaFeignClient.registrar(msRequest).stream()
                .map(this::toEstudianteBffDTO)
                .toList();
    }

    public List<EstudianteAsistenciaBffDTO> getPorCurso(String cursoId, LocalDate fecha) {
        return asistenciaFeignClient.getPorCurso(cursoId, fecha).stream()
                .map(this::toEstudianteBffDTO)
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
        return asistenciaFeignClient.getInasistencias().stream()
                .map(this::toInasistenciaBffDTO)
                .toList();
    }

    public InasistenciaBffDTO justificar(Long id, JustificacionBffRequestDTO request) {
        JustificacionMsRequestDTO msRequest = JustificacionMsRequestDTO.builder()
                .motivo(request.getMotivo())
                .build();
        return toInasistenciaBffDTO(asistenciaFeignClient.justificar(id, msRequest));
    }

    private EstudianteAsistenciaBffDTO toEstudianteBffDTO(AsistenciaMsResponseDTO ms) {
        return EstudianteAsistenciaBffDTO.builder()
                .id(ms.getId())
                .nombre(ms.getEstudianteId())
                .curso(ms.getCursoId())
                .estado(ms.getEstado() != null ? ms.getEstado().toLowerCase() : null)
                .hora(null)
                .build();
    }

    private HistorialAsistenciaBffDTO toHistorialBffDTO(AsistenciaMsResponseDTO ms) {
        return HistorialAsistenciaBffDTO.builder()
                .id(ms.getId())
                .fecha(ms.getFecha())
                .estado(ms.getEstado() != null ? ms.getEstado().toLowerCase() : null)
                .anotacion(ms.getObservacion())
                .build();
    }

    private InasistenciaBffDTO toInasistenciaBffDTO(AsistenciaMsResponseDTO ms) {
        return InasistenciaBffDTO.builder()
                .id(ms.getId())
                .fecha(ms.getFecha())
                .alumno(ms.getEstudianteId())
                .curso(ms.getCursoId())
                .justificada("JUSTIFICADO".equalsIgnoreCase(ms.getEstado()))
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
