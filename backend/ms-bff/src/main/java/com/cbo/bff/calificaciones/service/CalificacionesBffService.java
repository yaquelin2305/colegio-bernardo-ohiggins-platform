package com.cbo.bff.calificaciones.service;

import com.cbo.bff.calificaciones.dto.CalificacionBffResponseDTO;
import com.cbo.bff.calificaciones.dto.GuardarCalificacionBffRequestDTO;
import com.cbo.bff.calificaciones.dto.ms.GuardarCalificacionMsRequestDTO;
import com.cbo.bff.gestionacademica.client.AcademicoFeignClient;
import com.cbo.bff.gestionacademica.client.UsuarioFeignClient;
import com.cbo.bff.gestionacademica.dto.ms.UsuarioNombreMsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalificacionesBffService {

    private final AcademicoFeignClient academicoFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;

    public List<CalificacionBffResponseDTO> obtenerCalificaciones(Long cursoId, Long asignaturaId) {
        Map<String, String> cache = new HashMap<>();
        return academicoFeignClient.obtenerCalificacionesPorCursoYAsignatura(cursoId, asignaturaId)
                .stream()
                .map(ms -> CalificacionBffResponseDTO.builder()
                        .id(ms.getUsuarioUuid())
                        .nombre(cache.computeIfAbsent(ms.getUsuarioUuid(), this::resolverNombre))
                        .nota1(ms.getNota1())
                        .nota2(ms.getNota2())
                        .nota3(ms.getNota3())
                        .promedio(ms.getPromedio())
                        .build())
                .toList();
    }

    public void guardarCalificaciones(List<GuardarCalificacionBffRequestDTO> calificaciones) {
        calificaciones.forEach(c -> academicoFeignClient.guardarCalificacion(
                GuardarCalificacionMsRequestDTO.builder()
                        .usuarioUuid(c.getUsuarioUuid())
                        .asignaturaId(c.getAsignaturaId())
                        .nota1(c.getNota1())
                        .nota2(c.getNota2())
                        .nota3(c.getNota3())
                        .build()
        ));
    }

    private String resolverNombre(String uuid) {
        try {
            UsuarioNombreMsDTO res = usuarioFeignClient.obtenerNombre(uuid);
            return res != null ? res.getNombreCompleto() : uuid;
        } catch (Exception ex) {
            return uuid;
        }
    }
}
