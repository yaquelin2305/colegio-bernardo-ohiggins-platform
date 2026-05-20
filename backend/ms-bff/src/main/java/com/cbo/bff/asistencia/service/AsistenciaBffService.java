package com.cbo.bff.asistencia.service;

import com.cbo.bff.asistencia.dto.AlumnoBffDTO;
import com.cbo.bff.asistencia.dto.AnotacionBffDTO;
import com.cbo.bff.asistencia.dto.AsistenciaRequestBffDTO;
import com.cbo.bff.asistencia.dto.EstudianteAsistenciaBffDTO;
import com.cbo.bff.asistencia.dto.HistorialAsistenciaBffDTO;
import com.cbo.bff.asistencia.dto.InasistenciaBffDTO;
import com.cbo.bff.asistencia.dto.JustificacionBffRequestDTO;
import com.cbo.bff.asistencia.dto.ResumenAsistenciaBffDTO;
import com.cbo.bff.asistencia.client.AsistenciaFeignClient;
import com.cbo.bff.asistencia.dto.ms.AnotacionMsRequestDTO;
import com.cbo.bff.asistencia.dto.ms.AnotacionMsResponseDTO;
import com.cbo.bff.asistencia.dto.ms.AsistenciaMsRequestDTO;
import com.cbo.bff.asistencia.dto.ms.AsistenciaMsResponseDTO;
import com.cbo.bff.asistencia.dto.ms.JustificacionMsRequestDTO;
import com.cbo.bff.asistencia.dto.ms.ResumenAsistenciaMsDTO;
import com.cbo.bff.gestionacademica.client.AcademicoFeignClient;
import com.cbo.bff.gestionacademica.client.UsuarioFeignClient;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioMsDTO;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioNombreMsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AsistenciaBffService {

    private final AsistenciaFeignClient asistenciaFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;
    private final AcademicoFeignClient academicoFeignClient;

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
        String nombre = resolverNombre(estudianteId);
        return asistenciaFeignClient.getPorEstudiante(estudianteId).stream()
                .map(ms -> toHistorialBffDTO(ms, nombre))
                .toList();
    }

    public List<AlumnoBffDTO> getAlumnosPorCurso(Long cursoId) {
        Map<String, UsuarioMsDTO> cache = new HashMap<>();
        return academicoFeignClient.obtenerEstudiantesPorCurso(cursoId).stream()
                .map(m -> {
                    String uuid = (String) m.get("usuarioUuid");
                    UsuarioMsDTO u = cache.computeIfAbsent(uuid, id -> {
                        try { return usuarioFeignClient.obtenerPorId(UUID.fromString(id)); }
                        catch (Exception ex) { return null; }
                    });
                    return AlumnoBffDTO.builder()
                            .estudianteId(uuid)
                            .nombre(u != null ? u.getNombreCompleto() : uuid)
                            .rut(u != null ? u.getRut() : "")
                            .build();
                })
                .toList();
    }

    public ResumenAsistenciaBffDTO getResumen(String cursoId, LocalDate fecha) {
        ResumenAsistenciaMsDTO ms = asistenciaFeignClient.getResumen(cursoId, fecha);
        String nombreCurso = resolverNombreCurso(cursoId);
        return toResumenBffDTO(ms, nombreCurso);
    }

    public List<InasistenciaBffDTO> getInasistencias() {
        Map<String, String> nombreCache = new HashMap<>();
        Map<String, String> cursoCache = new HashMap<>();
        return asistenciaFeignClient.getInasistencias().stream()
                .map(ms -> toInasistenciaBffDTO(ms, nombreCache, cursoCache))
                .toList();
    }

    public InasistenciaBffDTO justificar(Long id, JustificacionBffRequestDTO request) {
        JustificacionMsRequestDTO msRequest = JustificacionMsRequestDTO.builder()
                .motivo(request.getMotivo())
                .build();
        AsistenciaMsResponseDTO ms = asistenciaFeignClient.justificar(id, msRequest);
        return toInasistenciaBffDTO(ms, new HashMap<>(), new HashMap<>());
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
            UsuarioNombreMsDTO res = usuarioFeignClient.obtenerNombre(uuid);
            return res != null ? res.getNombreCompleto() : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private HistorialAsistenciaBffDTO toHistorialBffDTO(AsistenciaMsResponseDTO ms, String nombre) {
        return HistorialAsistenciaBffDTO.builder()
                .id(ms.getId())
                .fecha(ms.getFecha())
                .estado(ms.getEstado() != null ? ms.getEstado().toLowerCase() : null)
                .anotacion(ms.getObservacion())
                .nombre(nombre)
                .build();
    }

    private InasistenciaBffDTO toInasistenciaBffDTO(AsistenciaMsResponseDTO ms,
                                                     Map<String, String> nombreCache,
                                                     Map<String, String> cursoCache) {
        String uuid = ms.getEstudianteId();
        String nombre = nombreCache.computeIfAbsent(uuid, this::resolverNombre);
        String cursoId = ms.getCursoId();
        String nombreCurso = cursoCache.computeIfAbsent(cursoId, this::resolverNombreCurso);
        return InasistenciaBffDTO.builder()
                .id(ms.getId())
                .fecha(ms.getFecha())
                .alumno(nombre != null ? nombre : uuid)
                .curso(nombreCurso != null ? nombreCurso : cursoId)
                .justificada("JUSTIFICADO".equalsIgnoreCase(ms.getEstado()))
                .build();
    }

    private String resolverNombreCurso(String cursoId) {
        try {
            return academicoFeignClient.listarCursos().stream()
                    .filter(c -> String.valueOf(c.get("id")).equals(cursoId))
                    .findFirst()
                    .map(c -> (String) c.get("nombre"))
                    .orElse(null);
        } catch (Exception ex) {
            return null;
        }
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

    private ResumenAsistenciaBffDTO toResumenBffDTO(ResumenAsistenciaMsDTO ms, String nombreCurso) {
        return ResumenAsistenciaBffDTO.builder()
                .total(ms.getTotal())
                .presentes(ms.getTotalPresentes())
                .ausentes(ms.getTotalAusentes())
                .totalJustificados(ms.getTotalJustificados())
                .porcentaje(ms.getPorcentajeAsistencia())
                .nombreCurso(nombreCurso)
                .build();
    }
}
