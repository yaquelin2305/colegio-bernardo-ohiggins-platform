package com.duoc.ms_asistencia.infrastructure.adapter.out.persistence;

import com.duoc.ms_asistencia.domain.model.Asistencia;
import com.duoc.ms_asistencia.domain.port.out.AsistenciaRepositoryPort;
import com.duoc.ms_asistencia.infrastructure.adapter.out.persistence.entity.AsistenciaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AsistenciaRepositoryAdapter implements AsistenciaRepositoryPort {

    private final AsistenciaJpaRepository jpaRepository;

  
    public AsistenciaRepositoryAdapter(AsistenciaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Asistencia> saveAll(List<Asistencia> asistencias) {
        // Mapeo manual de Dominio a Entidad (Protegiendo la BD)
        List<AsistenciaEntity> entities = asistencias.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        // Guardar y mapear de vuelta a Dominio (Protegiendo el Service)
        return jpaRepository.saveAll(entities).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Asistencia> findByCursoIdAndFecha(String cursoId, LocalDate fecha) {
        return jpaRepository.findByCursoIdAndFecha(cursoId, fecha).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Asistencia> findByEstudianteId(String estudianteId) {
        return jpaRepository.findByEstudianteId(estudianteId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Asistencia> findByEstadoIn(List<String> estados) {
        return jpaRepository.findByEstadoIn(estados).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Asistencia> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Asistencia save(Asistencia asistencia) {
        return toDomain(jpaRepository.save(toEntity(asistencia)));
    }

    // MAPEO MANUAL: De Dominio a Entidad JPA
    private AsistenciaEntity toEntity(Asistencia dom) {
        if (dom == null) return null;
        AsistenciaEntity ent = new AsistenciaEntity();
        ent.setId(dom.getId());
        ent.setEstudianteId(dom.getEstudianteId());
        ent.setCursoId(dom.getCursoId());
        ent.setFecha(dom.getFecha());
        ent.setEstado(dom.getEstado());
        ent.setObservacion(dom.getObservacion());
        return ent;
    }

  
    private Asistencia toDomain(AsistenciaEntity ent) {
        if (ent == null) return null;
        return Asistencia.builder()
                .id(ent.getId())
                .estudianteId(ent.getEstudianteId())
                .cursoId(ent.getCursoId())
                .fecha(ent.getFecha())
                .estado(ent.getEstado())
                .observacion(ent.getObservacion())
                .build();
    }
}