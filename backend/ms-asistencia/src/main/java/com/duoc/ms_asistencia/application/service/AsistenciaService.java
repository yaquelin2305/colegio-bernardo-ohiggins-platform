package com.duoc.ms_asistencia.application.service;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import com.duoc.ms_asistencia.domain.model.ResumenAsistencia;
import com.duoc.ms_asistencia.domain.port.in.AsistenciaUseCase;
import com.duoc.ms_asistencia.domain.port.out.AsistenciaRepositoryPort;
import com.duoc.ms_asistencia.domain.strategy.AsistenciaStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsistenciaService implements AsistenciaUseCase {

    private final AsistenciaRepositoryPort repositoryPort;
    private final List<AsistenciaStrategy> strategies;

    @Override
    public List<Asistencia> registrarLista(List<Asistencia> asistencias) {
        asistencias.forEach(a -> {
            if (a.getFecha() == null) a.setFecha(LocalDate.now());
            strategies.stream()
                .filter(s -> s.aplica(a.getEstado()))
                .forEach(s -> s.procesar(a));
        });
        return repositoryPort.saveAll(asistencias);
    }

    @Override
    public List<Asistencia> obtenerPorCursoYFecha(String cursoId, LocalDate fecha) {
        return repositoryPort.findByCursoIdAndFecha(cursoId, fecha);
    }

    @Override
    public List<Asistencia> obtenerPorEstudiante(String estudianteId) {
        return repositoryPort.findByEstudianteId(estudianteId);
    }

    @Override
    public List<Asistencia> obtenerInasistencias() {
        return repositoryPort.findByEstadoIn(List.of("AUSENTE", "JUSTIFICADO"));
    }

    @Override
    public ResumenAsistencia obtenerResumen(String cursoId, LocalDate fecha) {
        List<Asistencia> lista = repositoryPort.findByCursoIdAndFecha(cursoId, fecha);
        Map<String, Long> conteo = lista.stream()
            .collect(Collectors.groupingBy(a -> a.getEstado().toUpperCase(), Collectors.counting()));

        int presentes    = conteo.getOrDefault("PRESENTE", 0L).intValue();
        int ausentes     = conteo.getOrDefault("AUSENTE", 0L).intValue();
        int justificados = conteo.getOrDefault("JUSTIFICADO", 0L).intValue();
        int total        = lista.size();
        double porcentaje = total > 0 ? Math.round((presentes * 100.0 / total) * 10.0) / 10.0 : 0.0;

        return ResumenAsistencia.builder()
            .cursoId(cursoId)
            .fecha(fecha)
            .totalPresentes(presentes)
            .totalAusentes(ausentes)
            .totalJustificados(justificados)
            .total(total)
            .porcentajeAsistencia(porcentaje)
            .build();
    }

    @Override
    public Asistencia justificarInasistencia(Long id, String motivo) {
        Asistencia asistencia = repositoryPort.findById(id)
            .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));
        asistencia.setEstado("JUSTIFICADO");
        asistencia.setObservacion(motivo);
        strategies.stream()
            .filter(s -> s.aplica(asistencia.getEstado()))
            .forEach(s -> s.procesar(asistencia));
        return repositoryPort.save(asistencia);
    }
}