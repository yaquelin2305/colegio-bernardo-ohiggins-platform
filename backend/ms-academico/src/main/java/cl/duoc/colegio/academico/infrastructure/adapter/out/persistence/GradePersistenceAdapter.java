package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.application.port.out.GradeRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.domain.model.GradeContract;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.GradeEntity;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper.GradePersistenceMapper;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.GradeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para Notas.
 */
@Component
public class GradePersistenceAdapter implements GradeRepositoryPort {

    private final GradeJpaRepository jpaRepository;
    private final GradePersistenceMapper mapper;

    public GradePersistenceAdapter(GradeJpaRepository jpaRepository,
                                    GradePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Grade guardar(Grade grade) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(grade)));
    }

    @Override
    public Optional<Grade> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Grade> buscarPorUsuarioUuid(UUID usuarioUuid) {
        return jpaRepository.findByUsuarioUuid(usuarioUuid).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Grade> buscarPorUsuarioUuidYAsignaturaId(UUID usuarioUuid, Long asignaturaId) {
        return jpaRepository.findByUsuarioUuidAndAsignaturaId(usuarioUuid, asignaturaId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<GradeContract> buscarContratoPorUsuarioUuidYAsignaturaId(UUID usuarioUuid, Long asignaturaId) {
        return jpaRepository.findByUsuarioUuidAndAsignaturaId(usuarioUuid, asignaturaId)
                .stream().findFirst()
                .map(this::toContract);
    }

    @Override
    public GradeContract guardarContrato(GradeContract contrato) {
        Optional<GradeEntity> existente = jpaRepository
                .findByUsuarioUuidAndAsignaturaId(contrato.getUsuarioUuid(), contrato.getAsignaturaId())
                .stream().findFirst();

        GradeEntity entity = existente.orElse(GradeEntity.builder()
                .usuarioUuid(contrato.getUsuarioUuid())
                .asignaturaId(contrato.getAsignaturaId())
                .build());

        entity.setNota1(contrato.getNota1());
        entity.setNota2(contrato.getNota2());
        entity.setNota3(contrato.getNota3());
        entity.setPromedio(contrato.getPromedio());
        GradeEntity saved = jpaRepository.save(entity);

        return toContract(saved);
    }

    @Override
    public List<GradeContract> buscarContratosPorUsuarioUuid(UUID usuarioUuid) {
        return jpaRepository.findByUsuarioUuid(usuarioUuid).stream()
                .map(this::toContract)
                .collect(Collectors.toList());
    }

    private GradeContract toContract(GradeEntity entity) {
        return new GradeContract(
                entity.getUsuarioUuid(),
                entity.getAsignaturaId(),
                entity.getNota1(),
                entity.getNota2(),
                entity.getNota3(),
                entity.getPromedio());
    }
}
