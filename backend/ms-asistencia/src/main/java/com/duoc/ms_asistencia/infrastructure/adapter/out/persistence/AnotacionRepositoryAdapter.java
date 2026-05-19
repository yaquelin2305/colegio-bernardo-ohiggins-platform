package com.duoc.ms_asistencia.infrastructure.adapter.out.persistence;

import com.duoc.ms_asistencia.domain.model.Anotacion;
import com.duoc.ms_asistencia.domain.port.out.AnotacionRepositoryPort;
import com.duoc.ms_asistencia.infrastructure.adapter.out.persistence.entity.AnotacionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnotacionRepositoryAdapter implements AnotacionRepositoryPort {

    private final AnotacionJpaRepository jpaRepository;

    public AnotacionRepositoryAdapter(AnotacionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Anotacion guardar(Anotacion anotacion) {
        return toDomain(jpaRepository.save(toEntity(anotacion)));
    }

    @Override
    public List<Anotacion> buscarPorEstudiante(String estudianteId) {
        return jpaRepository.findByEstudianteId(estudianteId).stream().map(this::toDomain).toList();
    }

    private Anotacion toDomain(AnotacionEntity e) {
        return Anotacion.builder()
                .id(e.getId())
                .estudianteId(e.getEstudianteId())
                .tipo(e.getTipo())
                .descripcion(e.getDescripcion())
                .fecha(e.getFecha())
                .build();
    }

    private AnotacionEntity toEntity(Anotacion a) {
        AnotacionEntity e = new AnotacionEntity();
        e.setId(a.getId());
        e.setEstudianteId(a.getEstudianteId());
        e.setTipo(a.getTipo());
        e.setDescripcion(a.getDescripcion());
        e.setFecha(a.getFecha());
        return e;
    }
}
