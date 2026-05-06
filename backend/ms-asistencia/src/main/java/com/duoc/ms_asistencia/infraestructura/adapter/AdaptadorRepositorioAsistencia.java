package com.duoc.ms_asistencia.infraestructura.adapter;

import com.duoc.ms_asistencia.aplicacion.port.PuertoRepositorioAsistencia;
import com.duoc.ms_asistencia.dominio.entity.Asistencia;
import com.duoc.ms_asistencia.dominio.entity.EstadoAsistencia;
import com.duoc.ms_asistencia.infraestructura.entity.AsistenciaJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class AdaptadorRepositorioAsistencia implements PuertoRepositorioAsistencia {
    private final RepositorioJpaAsistencia jpaRepository;

    public AdaptadorRepositorioAsistencia(RepositorioJpaAsistencia jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Asistencia guardar(Asistencia asistencia) {
        AsistenciaJpa jpa = convertirAJpa(asistencia);
        AsistenciaJpa guardado = jpaRepository.save(jpa);
        return convertirADominio(guardado);
    }

    @Override
    public Optional<Asistencia> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::convertirADominio);
    }

    @Override
    public Page<Asistencia> buscarPorEstudianteId(Long estudianteId, Pageable pageable) {
        return jpaRepository.findByEstudianteId(estudianteId, pageable).map(this::convertirADominio);
    }

    @Override
    public Page<Asistencia> buscarPorFecha(LocalDate fecha, Pageable pageable) {
        return jpaRepository.findByFecha(fecha, pageable).map(this::convertirADominio);
    }

    private AsistenciaJpa convertirAJpa(Asistencia asistencia) {
        AsistenciaJpa jpa = new AsistenciaJpa(
                asistencia.getEstudianteId(),
                asistencia.getFecha(),
                asistencia.getEstado(),
                asistencia.getObservacion()
        );
        jpa.setId(asistencia.getId());
        return jpa;
    }

    private Asistencia convertirADominio(AsistenciaJpa jpa) {
        Asistencia asistencia = new Asistencia(
                jpa.getEstudianteId(),
                jpa.getFecha(),
                jpa.getEstado(),
                jpa.getObservacion()
        );
        asistencia.setId(jpa.getId());
        return asistencia;
    }
}