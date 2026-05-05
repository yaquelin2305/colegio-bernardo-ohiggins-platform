package com.duoc.ms_comunicaciones.infrastructure.adapter.out.persistence;

import com.duoc.ms_comunicaciones.domain.model.Comunicacion;
import com.duoc.ms_comunicaciones.domain.model.Canal;
import com.duoc.ms_comunicaciones.domain.port.out.ComunicacionRepositoryPort;
import com.duoc.ms_comunicaciones.infrastructure.adapter.out.persistence.entity.ComunicacionEntity;
import com.duoc.ms_comunicaciones.infrastructure.adapter.out.persistence.entity.CanalEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ComunicacionRepositoryAdapter implements ComunicacionRepositoryPort {

    private final ComunicacionJpaRepository jpaRepository;

    public ComunicacionRepositoryAdapter(ComunicacionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Comunicacion save(Comunicacion dom) {
        ComunicacionEntity entity = toEntity(dom);
        ComunicacionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Comunicacion> findByUsuarioId(String usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Comunicacion> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Comunicacion updateLeido(Long id, boolean leido) {
        ComunicacionEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));
        entity.setLeido(leido);
        return toDomain(jpaRepository.save(entity));
    }

   
    private ComunicacionEntity toEntity(Comunicacion dom) {
        ComunicacionEntity ent = new ComunicacionEntity();
        ent.setId(dom.getId());
        ent.setUsuarioId(dom.getUsuarioId());
        ent.setDestinatario(dom.getDestinatario());
        ent.setAsunto(dom.getAsunto());
        ent.setMensaje(dom.getMensaje());
        ent.setCanal(CanalEntity.valueOf(dom.getCanal().name()));
        ent.setFechaEnvio(dom.getFechaEnvio());
        ent.setLeido(dom.isLeido());
        return ent;
    }

   
    private Comunicacion toDomain(ComunicacionEntity ent) {
        Comunicacion dom = new Comunicacion();
        dom.setId(ent.getId());
        dom.setUsuarioId(ent.getUsuarioId());
        dom.setDestinatario(ent.getDestinatario());
        dom.setAsunto(ent.getAsunto());
        dom.setMensaje(ent.getMensaje());
        dom.setCanal(Canal.valueOf(ent.getCanal().name()));
        dom.setFechaEnvio(ent.getFechaEnvio());
        dom.setLeido(ent.isLeido());
        return dom;
    }
}